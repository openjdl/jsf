package org.kidal.jsf.graphql;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import graphql.AssertException;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.AsyncSerialExecutionStrategy;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.language.ScalarTypeDefinition;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kidal.jsf.core.exception.JsfException;
import org.kidal.jsf.core.exception.JsfExceptions;
import org.kidal.jsf.core.utils.IOUtils;
import org.kidal.jsf.core.utils.ReflectionUtils;
import org.kidal.jsf.core.utils.SpringUtils;
import org.kidal.jsf.core.utils.StringUtils;
import org.kidal.jsf.graphql.annotation.GraphqlFetcher;
import org.kidal.jsf.graphql.annotation.GraphqlSchema;
import org.kidal.jsf.graphql.boot.JsfGraphqlProperties;
import org.kidal.jsf.graphql.fetcher.*;
import org.kidal.jsf.graphql.query.*;
import org.kidal.jsf.graphql.scalar.DateCoercing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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
  private final JsfGraphqlProperties properties;

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
  @NotNull
  private final ConversionService conversionService;

  /**
   *
   */
  private GraphQL graphql;

  /**
   *
   */
  public GraphqlServiceImpl(@NotNull JsfGraphqlProperties properties,
                            @NotNull SpringUtils springUtils,
                            @Nullable ThreadPoolTaskExecutor threadPoolTaskExecutor,
                            @NotNull ConversionService conversionService) {
    this.registerSelf();
    this.properties = properties;
    this.springUtils = springUtils;
    this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    this.conversionService = conversionService;
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
  public void initializeJsfService() throws Exception {
    // 添加额外的内建类型
    ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS.add(DateCoercing.GraphQLDate);
    ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS_DEFINITIONS.put("Date", new ScalarTypeDefinition("Date"));

    // 注册格式和Fetcher
    final TypeDefinitionRegistry typeDefinitionRegistry = createTypeDefinitionRegistry();
    final RuntimeWiring runtimeWiring = createRuntimeWiring();

    // 创建
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
  @NotNull
  private TypeDefinitionRegistry createTypeDefinitionRegistry() throws IOException {
    SchemaParser schemaParser = new SchemaParser();
    TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();

    // 内建
    URL url = getClass().getClassLoader().getResource("graphql/built-in.graphql");
    if (url != null) {
      String builtIn = IOUtils.readAllText(ResourceUtils.getFile(url));
      typeDefinitionRegistry.merge(schemaParser.parse(builtIn));
    }

    // 用户
    if (properties.getPathsToScan().size() > 0) {
      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      List<Resource> resources = new ArrayList<>();
      for (String path : properties.getPathsToScan()) {
        Resource[] resolverResources = resolver.getResources(path);
        Collections.addAll(resources, resolverResources);
      }
      for (Resource resource : resources) {
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
          typeDefinitionRegistry.merge(schemaParser.parse(reader));
        } catch (Exception e) {
          throw new IllegalStateException("Merge schema file(" + resource.getFilename() +
            ", " + resource.getDescription() + " failed", e);
        }
      }
    }

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

    // 内建
    wiring.directive(EmptyMap.Directive.NAME, new EmptyMap.Directive());
    wiring.directive(ByteUnitFetcher.Directive.NAME, new ByteUnitFetcher.Directive());
    wiring.directive(TimeUnitFetcher.Directive.NAME, new TimeUnitFetcher.Directive());
    wiring.directive(DateUnitFetcher.Directive.NAME, new DateUnitFetcher.Directive());

    // 用户
    Map<String, List<DelegatedDataFetcher>> fetchersMap = Maps.newHashMap();

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

        ReflectionUtils.doWithMethods(bean.getClass(),
          method -> {
            final GraphqlFetcher fieldAnnotation = method.getAnnotation(GraphqlFetcher.class);
            final String fieldName = StringUtils.isBlank(fieldAnnotation.value())
              ? method.getName()
              : fieldAnnotation.value();

            // 类型处理
            DelegatedDataFetcher fetcher = new DelegatedDataFetcher(typeName, fieldName, bean, method);
            if (fieldAnnotation.unitFactory() != BaseUnitFetcherFactory.class) {
              BaseUnitFetcherFactory factory = UnitFetcherFactoryStaticRegistry.get(fieldAnnotation.unitFactory());
              if (factory != null) {
                fetcher = (DelegatedDataFetcher) factory.withUnitFetcher(fetcher);
              }
            }

            // 添加
            List<DelegatedDataFetcher> fetchers = fetchersMap.computeIfAbsent(typeName, k -> Lists.newArrayList());
            fetchers.add(fetcher);
          },
          method -> method.isAnnotationPresent(GraphqlFetcher.class)
        );
      });

    fetchersMap.forEach((typeName, fetchers) ->
      wiring.type(typeName, builder -> {
        fetchers.forEach(fetcher -> builder.dataFetcher(fetcher.getFieldName(), fetcher));
        return builder;
      })
    );

    // done
    return wiring.build();
  }

  /**
   *
   */
  @NotNull
  private GraphqlFetchingContext createFetchingContext(@NotNull GraphqlQueryArgs args) {
    return new GraphqlFetchingContext(args.getPassport(), args.getClientIp(), args.getXVariables(), conversionService);
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
