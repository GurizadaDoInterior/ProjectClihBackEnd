package com.gurizadadointerior.clih.user.application;
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Base64;
import javax.imageio.ImageIO;
import com.gurizadadointerior.clih.shared.error.*;
import com.gurizadadointerior.clih.user.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ProfilePhotoService {
 private final AppUserRepository repository;
 public ProfilePhotoService(AppUserRepository repository) { this.repository = repository; }
 @Transactional public AppUser save(AppUser user, String image) {
  user.setAvatarImage(process(image));
  return repository.save(user);
 }
 @Transactional public AppUser remove(AppUser user) {
  user.setAvatarImage(null);
  user.updateProfile(null, null, "", null, null);
  return repository.save(user);
 }
 static byte[] process(String image) {
  try {
   if (image == null || image.length() > 1_400_000 || !image.startsWith("data:image/")) throw new IllegalArgumentException();
   int comma = image.indexOf(',');
   byte[] bytes = Base64.getDecoder().decode(image.substring(comma + 1));
   try (var input = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
    var readers = ImageIO.getImageReaders(input);
    if (!readers.hasNext()) throw new IllegalArgumentException();
    var reader = readers.next();
    try {
     reader.setInput(input);
     int w = reader.getWidth(0), h = reader.getHeight(0);
     if (w < 1 || h < 1 || (long) w * h > 16_000_000) throw new IllegalArgumentException();
     var source = reader.read(0);
     var output = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
     var graphics = output.createGraphics();
     graphics.setColor(Color.WHITE); graphics.fillRect(0, 0, 512, 512);
     graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
     int side = Math.min(w, h), x = (w - side) / 2, y = (h - side) / 2;
     graphics.drawImage(source, 0, 0, 512, 512, x, y, x + side, y + side, null);
     graphics.dispose();
     var buffer = new ByteArrayOutputStream();
     ImageIO.write(output, "jpeg", buffer);
     return buffer.toByteArray();
    } finally { reader.dispose(); }
   }
  } catch (Exception exception) { throw new ApiException(ErrorCode.INVALID_PROFILE_PHOTO); }
 }
}
