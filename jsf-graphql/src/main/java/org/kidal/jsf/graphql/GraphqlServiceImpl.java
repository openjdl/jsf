package org.kidal.jsf.graphql;

import com.google.common.collect.Lists;
import graphql.AssertException;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.AsyncSerialExecutionStrategy;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kidal.jsf.core.exception.JsfException;
import org.kidal.jsf.core.exception.JsfExceptions;
import org.kidal.jsf.core.utils.ReflectionUtils;
import org.kidal.jsf.core.utils.SpringUtils;
import org.kidal.jsf.core.utils.StringUtils;
import org.kidal.jsf.graphql.annotation.GraphqlFetcher;
import org.kidal.jsf.graphql.annotation.GraphqlSchema;
import org.kidal.jsf.graphql.fetcher.*;
import org.kidal.jsf.graphql.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created at 2020-08-05 21:26:15
 *
 * @author kidal
 * @since 0.1.0
 */
public class GraphqlServiceImpl implements GraphqlService {
  /**
   * 日志
   */
  private static final Logger LOG = LoggerFactory.getLogger(GraphqlServiceImpl.class);

  /**
   * 无视的异常信息
   */
  private static final String IGNORED_ASSERT_EXCEPTION_MESSAGE = "wrappedType can't be null";

  /**
   *
   */
  @NotNull
  private final SpringUtils springUtils;

  /**
   *
   */
  @Nullable
  private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

  /**
   *
   */
  private GraphQL graphql;

  /**
   *
   */
  public GraphqlServiceImpl(@NotNull SpringUtils springUtils,
                            @Nullable ThreadPoolTaskExecutor threadPoolTaskExecutor) {
    this.registerSelf();
    this.springUtils = springUtils;
    this.threadPoolTaskExecutor = threadPoolTaskExecutor;
  }

  /**
   *
   */
  @NotNull
  @Override
  public String getJsfServiceName() {
    return "GraphqlService";
  }

  /**
   *
   */
  @Override
  public void initializeJsfService() {
    // TODO: 添加Date类型

    final TypeDefinitionRegistry typeDefinitionRegistry = createTypeDefinitionRegistry();
    final RuntimeWiring runtimeWiring = createRuntimeWiring();
    final SchemaGenerator schemaGenerator = new SchemaGenerator();

    final GraphQLSchema schema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

    graphql = GraphQL.newGraphQL(schema)
      .queryExecutionStrategy(new AsyncExecutionStrategy())
      .mutationExecutionStrategy(new AsyncSerialExecutionStrategy())
      .subscriptionExecutionStrategy(new SubscriptionExecutionStrategy())
      .build();
  }

  /**
   * 创建类型定义注册器
   */
  private TypeDefinitionRegistry createTypeDefinitionRegistry() {
    SchemaParser schemaParser = new SchemaParser();
    TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();

    springUtils.getAllBeans(true)
      .forEach(bean -> {
        if (bean.getClass().isAnnotationPresent(GraphqlSchema.class)) {
          ReflectionUtils.doWithMethods(
            bean.getClass(),
            method -> {
              if (method.getReturnType() == String.class && method.getParameterCount() == 0) {
                String schemaInput = null;
                try {
                  schemaInput = (String) method.invoke(bean);
                } catch (InvocationTargetException e) {
                  LOG.error("", e);
                }
                if (schemaInput != null) {
                  typeDefinitionRegistry.merge(schemaParser.parse(schemaInput));
                }
              }
            },
            method -> method.isAnnotationPresent(GraphqlSchema.class)
          );
        }
      });

    return typeDefinitionRegistry;
  }

  /**
   *
   */
  private RuntimeWiring createRuntimeWiring() {
    final RuntimeWiring.Builder wiring = RuntimeWiring.newRuntimeWiring();

    // fetchers
    springUtils
      .getAllBeans(true)
      .forEach(bean -> {
        final GraphqlFetcher typeAnnotation = AnnotationUtils.findAnnotation(bean.getClass(), GraphqlFetcher.class);
        if (typeAnnotation == null) {
          return;
        }

        final String typeName = StringUtils.isBlank(typeAnnotation.value())
          ? bean.getClass().getSimpleName()
          : typeAnnotation.value();

        wiring.type(typeName, builder -> {
          ReflectionUtils.doWithMethods(bean.getClass(),
            method -> {
              final GraphqlFetcher fieldAnnotation = method.getAnnotation(GraphqlFetcher.class);
              final String fieldName = StringUtils.isBlank(fieldAnnotation.value())
                ? method.getName()
                : fieldAnnotation.value();

              // 类型处理
              DelegatedDataFetcher fetcher = new DelegatedDataFetcher(bean, method);
              if (fieldAnnotation.unitFactory() != BaseUnitFetcherFactory.class) {
                BaseUnitFetcherFactory factory = UnitFetcherFactoryStaticRegistry.get(fieldAnnotation.unitFactory());
                if (factory != null) {
                  fetcher = (DelegatedDataFetcher) factory.withUnitFetcher(fetcher);
                }
              }

              // 注册fetcher
              builder.dataFetcher(fieldName, fetcher);
            },
            method -> method.isAnnotationPresent(GraphqlFetcher.class)
          );

          return builder;
        });
      });

    // directives
    wiring.directive("map", new EmptyMap.Directive());
    wiring.directive("byte", new ByteUnitFetcher.Directive());

    // done
    return wiring.build();
  }

  /**
   *
   */
  @NotNull
  private GraphqlFetchingContext createFetchingContext(@NotNull GraphqlQueryArgs args) {
    return new GraphqlFetchingContext(args.getPassport(), args.getClientIp(), args.getXVariables());
  }

  /**
   *
   */
  @NotNull
  @Override
  public GraphqlQueryResults query(@NotNull GraphqlQueryArgs args) {
    final GraphqlFetchingContext context = createFetchingContext(args);
    final ExecutionInput executionInput = ExecutionInput.newExecutionInput()
      .query(args.getQuery())
      .variables(args.getVariables())
      .context(context)
      .build();

    ExecutionResult executionResult;
    try {
      executionResult = graphql.execute(executionInput);
    } catch (Exception e) {
      if (e instanceof AssertException) {
        if (IGNORED_ASSERT_EXCEPTION_MESSAGE.equals(e.getMessage())) {
          throw new JsfException(JsfExceptions.BAD_REQUEST, e);
        }
      }
      throw e;
    }

    // 转换警告
    final Map<GraphqlFetchingWarningType, List<GraphqlFetchingWarning>> warnings = context
      .getWarnings()
      .entrySet()
      .stream()
      .collect(Collectors.toMap(Map.Entry::getKey, entry -> Lists.newArrayList(entry.getValue())));

    // done
    return new GraphqlQueryResults(executionResult, warnings, context.getCookies());
  }

  /**
   *
   */
  @NotNull
  public SpringUtils getSpringUtils() {
    return springUtils;
  }

  /**
   *
   */
  @Nullable
  public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
    return threadPoolTaskExecutor;
  }

  /**
   *
   */
  public GraphQL getGraphql() {
    return graphql;
  }
}