package com.gurizadadointerior.clih.user.application;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import javax.imageio.ImageIO;
import com.gurizadadointerior.clih.shared.error.ApiException;
class ProfilePhotoServiceTest {
 @Test void rejectsInvalidImageAndOversizedPayload() {
  assertThrows(ApiException.class, () -> ProfilePhotoService.process("data:image/jpeg;base64,bm90IGFuIGltYWdl"));
  assertThrows(ApiException.class, () -> ProfilePhotoService.process("data:image/jpeg;base64," + "A".repeat(1_400_000)));
 }
 @Test void cropsAndReencodesTo512PixelJpeg() throws Exception {
  var input = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
  var bytes = new ByteArrayOutputStream(); ImageIO.write(input, "png", bytes);
  var output = ProfilePhotoService.process("data:image/png;base64," + Base64.getEncoder().encodeToString(bytes.toByteArray()));
  var photo = ImageIO.read(new ByteArrayInputStream(output));
  assertEquals(512, photo.getWidth()); assertEquals(512, photo.getHeight());
  assertEquals(0xff, output[0] & 0xff); assertEquals(0xd8, output[1] & 0xff);
 }
}
