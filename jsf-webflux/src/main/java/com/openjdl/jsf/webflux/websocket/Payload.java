package com.openjdl.jsf.webflux.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.openjdl.jsf.core.exception.JsfException;
import com.openjdl.jsf.core.exception.JsfExceptionDataContract;
import com.openjdl.jsf.core.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Created at 2020-08-12 10:55:06
 *
 * @author kidal
 * @since 0.1.0
 */
public class Payload {
  /**
   * 版本号
   */
  @NotNull
  private String version = "JSF 1.0";

  /**
   * 消息ID
   */
  @NotNull
  private String id = "";

  /**
   * 消息类型
   */
  @NotNull
  private String type = "";

  /**
   * 错误信息
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Nullable
  private Error error;

  /**
   * 消息数据
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Nullable
  private Object data;

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  /**
   * 创建载荷
   */
  @NotNull
  public static Payload of(@NotNull String type, @Nullable Error error, @Nullable Object data) {
    Payload payload = new Payload();
    payload.setId(UUID.randomUUID().toString().replace("-", "").toLowerCase());
    payload.setType(type);
    payload.setError(error);
    payload.setData(data);
    return payload;
  }

  /**
   * 创建载荷
   */
  @NotNull
  public static Payload of(@NotNull String type, @Nullable Error error) {
    return of(type, error, null);
  }

  /**
   * s
   * 创建载荷
   */
  @NotNull
  public static Payload of(@NotNull String type, @Nullable Object data) {
    return of(type, null, data);
  }

  /**
   * 从字符串创建载荷
   */
  @NotNull
  public static Payload of(@NotNull String rawPayload) {
    return JsonUtils.toObject(rawPayload, Payload.class);
  }

  /**
   * 转换为载荷字符串
   */
  @NotNull
  public String toRawPayload() {
    return JsonUtils.toPrettyString(this);
  }

  /**
   * 转换为应答载荷
   */
  @NotNull
  public Payload toResponse(@Nullable Error error, @Nullable Object data) {
    Payload payload = new Payload();
    payload.setVersion(version);
    payload.setId(id);
    payload.setType(type);
    payload.setError(error);
    payload.setData(data);
    return payload;
  }

  /**
   * 转换为应答载荷
   */
  @NotNull
  public Payload toResponse(@NotNull Error error) {
    return toResponse(error, null);
  }

  /**
   * 转换为应答载荷
   */
  @NotNull
  public Payload toResponse(@Nullable Object data) {
    return toResponse(null, data);
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  @NotNull
  public String getVersion() {
    return version;
  }

  public void setVersion(@NotNull String version) {
    this.version = version;
  }

  @NotNull
  public String getId() {
    return id;
  }

  public void setId(@NotNull String id) {
    this.id = id;
  }

  @NotNull
  public String getType() {
    return type;
  }

  public void setType(@NotNull String type) {
    this.type = type;
  }

  @Nullable
  public Error getError() {
    return error;
  }

  public void setError(@Nullable Error error) {
    this.error = error;
  }

  @Nullable
  public Object getData() {
    return data;
  }

  public void setData(@Nullable Object data) {
    this.data = data;
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  public static class Error {
    /**
     * 错误ID
     */
    private long id;

    /**
     * 错误码
     */
    @NotNull
    private String code = "";

    /**
     * 错误消息
     */
    @NotNull
    private String message = "";

    /**
     *
     */
    public Error() {

    }

    /**
     *
     */
    public Error(long id, @NotNull String code, @NotNull String message) {
      this.id = id;
      this.code = code;
      this.message = message;
    }

    /**
     *
     */
    public Error(@NotNull JsfExceptionDataContract data) {
      this(data.getId(), data.getCode(), data.getFormat());
    }

    /**
     *
     */
    public Error(@NotNull JsfException e) {
      this(e.getData().getId(), e.getData().getCode(), e.formatMessage());
    }

    /**
     *
     */
    public long getId() {
      return id;
    }

    /**
     *
     */
    public void setId(int id) {
      this.id = id;
    }

    /**
     *
     */
    @NotNull
    public String getCode() {
      return code;
    }

    /**
     *
     */
    public void setCode(@NotNull String code) {
      this.code = code;
    }

    /**
     *
     */
    @NotNull
    public String getMessage() {
      return message;
    }

    /**
     *
     */
    public void setMessage(@NotNull String message) {
      this.message = message;
    }
  }
}
