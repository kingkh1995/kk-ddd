package com.kk.ddd.support.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

/**
 * 二维码工具类 <br>
 *
 * @author KaiKoo
 */
@Slf4j
public final class QRCodeUtils {

  private QRCodeUtils() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  // 默认宽度
  private static final int DEFAULT_WIDTH = 430;

  // 默认宽度
  private static final int DEFAULT_HEIGHT = 430;

  // 默认LOGO面积占比（%）
  private static final int DEFAULT_PERCENT = 30;

  // 默认生成图片格式
  private static final String DEFAULT_FORMAT = "PNG";

  // 默认logo圆角度数
  private static final int DEFAULT_CORNER_RADIUS = 30;

  /**
   * 生成带二维码并转换为Base64字符串输出 <br>
   * 在编码生成的字符串前部添加加密信息供前端解析（如jpg文件：data:image/jpg;base64）
   */
  public static String generateQRCodeToBase64Str(@NotBlank String text) {
    try (var bos = new ByteArrayOutputStream()) {
      generateQRCodeToStream(text, bos);
      return Base64.getEncoder().encodeToString(bos.toByteArray());
    } catch (IOException | WriterException e) {
      log.error("generate QRCode to base64 string error!", e);
      return null;
    }
  }

  /**
   * 生成二维码并写入输出流（使用默认参数） <br>
   * （需要手动关闭流）
   *
   * @param text
   * @param outputStream
   */
  public static void generateQRCodeToStream(
      @NotBlank String text, @NotNull OutputStream outputStream)
      throws IOException, WriterException {
    generateQRCodeToStream(text, outputStream, DEFAULT_FORMAT, DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }

  /**
   * 生成二维码并写入输出流
   *
   * @param text
   * @param outputStream 图片输出流
   * @param format 生成二维码图片格式
   * @param width 二维码宽度
   * @param height 二维码高度
   */
  public static void generateQRCodeToStream(
      @NotBlank String text,
      @NotNull OutputStream outputStream,
      @NotBlank String format,
      int width,
      int height)
      throws WriterException, IOException {
    // 生成二维码矩阵
    var bitMatrix = generateBitMatrix(text, width, height);
    // 写入输出流
    MatrixToImageWriter.writeToStream(bitMatrix, format, outputStream);
  }

  /**
   * 生成带logo的二维码并转换为Base64字符串输出
   *
   * @param text
   * @param logoImage logo图像
   */
  public static String generateQRCodeWithLogoToBase64Str(
      @NotBlank String text, @NotNull BufferedImage logoImage) {
    try (var bos = new ByteArrayOutputStream()) {
      generateQRCodeWithLogoToStream(text, bos, logoImage);
      return Base64.getEncoder().encodeToString(bos.toByteArray());
    } catch (IOException | WriterException e) {
      log.error("generate QRCode to base64 string error!", e);
      return null;
    }
  }

  /**
   * 生成带logo的二维码并写入输出流 （使用默认参数） <br>
   * （需要手动关闭流）
   */
  public static void generateQRCodeWithLogoToStream(
      @NotBlank String text, @NotNull OutputStream outputStream, @NotNull BufferedImage logoImage)
      throws IOException, WriterException {
    generateQRCodeWithLogoToStream(text, outputStream, DEFAULT_WIDTH, DEFAULT_HEIGHT, logoImage);
  }

  /**
   * 生成带logo的二维码并写入输出流
   *
   * @param text
   * @param outputStream 图片输出流
   * @param width 二维码宽度
   * @param height 二维码高度
   * @param logoImage logo图像 （使用ImageIO.read()方法生成）
   */
  public static void generateQRCodeWithLogoToStream(
      @NotBlank String text,
      @NotNull OutputStream outputStream,
      int width,
      int height,
      @NotNull BufferedImage logoImage)
      throws IOException, WriterException {
    // 生成二维码矩阵
    var bitMatrix = generateBitMatrix(text, width, height);
    // 添加logo并生成图像
    var output = addLogoToMatrix(bitMatrix, DEFAULT_PERCENT, logoImage, DEFAULT_CORNER_RADIUS);
    // 写入输出流
    ImageIO.write(output, DEFAULT_FORMAT, outputStream);
    // 释放图像缓冲区
    output.flush();
    logoImage.flush();
  }

  // 二维码生成writer
  private static final Writer WRITER;

  // hints配置
  private static final Map<EncodeHintType, Object> HINTS;

  // 矩阵转绘图对象配置
  private static final MatrixToImageConfig CONFIG;

  static {
    // 默认为 QRCodeWriter
    WRITER = new QRCodeWriter();
    HINTS = new HashMap<>();
    // 设置编码格式
    HINTS.put(EncodeHintType.CHARACTER_SET, "UTF-8");
    // 设置纠错等级最高
    HINTS.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
    // 设置白边大小
    HINTS.put(EncodeHintType.MARGIN, 1);
    // 保留logo图片原始颜色
    CONFIG = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
  }

  // 生成二维码矩阵
  private static BitMatrix generateBitMatrix(String text, int width, int height)
      throws WriterException {
    return WRITER.encode(text, BarcodeFormat.QR_CODE, width, height, HINTS);
  }

  // 给矩阵添加logo
  private static BufferedImage addLogoToMatrix(
      BitMatrix bitMatrix, int logoAreaPercent, BufferedImage logoImage, int cornerRadius) {
    // logo大小必须小于30%，不能高于二维码的最大纠错等级（30%）
    if (logoAreaPercent > 30) {
      throw new IllegalArgumentException("logo面积百分比必须小于等于30");
    }
    // 创建绘图对象
    var output = MatrixToImageWriter.toBufferedImage(bitMatrix, CONFIG);
    // logo美化
    // 生成圆角
    var logo0 = makeRoundedCorner(logoImage, cornerRadius, 0, Color.white);
    // 加上白色边框
    var logo = makeRoundedCorner(logo0, cornerRadius, 10, Color.white);
    // 绘图
    var g2 = output.createGraphics();
    var w = output.getWidth();
    var h = output.getHeight();
    var x = w * (100 - logoAreaPercent) / 200;
    var y = h * (100 - logoAreaPercent) / 200;
    g2.drawImage(logo, x, y, w - 2 * x, h - 2 * y, null);
    // 释放资源
    g2.dispose();
    logo.flush();
    logo0.flush();
    return output;
  }

  /**
   * 图片设置圆角 <br>
   * 实现方式：绘制一个纯色的带圆角的新图片，再将原始图片绘制到新图片上
   *
   * @param image
   * @param cornerRadius 圆角度数
   * @param margin 边距占比（%）设置为0则无边框
   * @param color 边框颜色
   * @return
   */
  private static BufferedImage makeRoundedCorner(
      BufferedImage image, int cornerRadius, int margin, Color color) {
    var w = image.getWidth();
    var h = image.getHeight();
    // 构建一个原始图片尺寸的新绘图对象
    var output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    // 绘图
    var g2 = output.createGraphics();
    g2.setComposite(AlphaComposite.Src);
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // 设置图片颜色
    g2.setColor(color);
    // 设置圆角
    g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
    // 将原始图片绘制到新的图片上面
    var x = w * margin / 200;
    var y = h * margin / 200;
    g2.setComposite(AlphaComposite.SrcAtop);
    g2.drawImage(image, x, y, w - 2 * x, h - 2 * y, null);
    g2.dispose();
    return output;
  }
}
