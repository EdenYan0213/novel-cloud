package io.github.xxyopen.novel.resource.util;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImgVerifyCodeUtilsTest {

    @Test
    void genVerifyCodeImg_shouldGenerateValidBase64Image() throws IOException {
        // Given
        String verifyCode = "123456";

        // When
        String base64Image = ImgVerifyCodeUtils.genVerifyCodeImg(verifyCode);

        // Then
        assertThat(base64Image).isNotNull().isNotEmpty();
        // Verify it's valid Base64
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        assertThat(imageBytes).isNotNull();

        // Optionally display the image
        displayImage(imageBytes, "test_verify_code.jpg");
    }

    @Test
    void genVerifyCodeImg_withEmptyString_shouldGenerateImage() throws IOException {
        // Given
        String verifyCode = "";

        // When
        String base64Image = ImgVerifyCodeUtils.genVerifyCodeImg(verifyCode);

        // Then
        assertThat(base64Image).isNotNull().isNotEmpty();

        // Optionally display the image
        displayImage(Base64.getDecoder().decode(base64Image), "test_empty_code.jpg");
    }

    @Test
    void genVerifyCodeImg_withNull_shouldThrowException() {
        // Given
        String verifyCode = null;

        // When/Then
        assertThatThrownBy(() -> ImgVerifyCodeUtils.genVerifyCodeImg(verifyCode))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void genVerifyCodeImg_withSpecialCharacters_shouldGenerateImage() throws IOException {
        // Given
        String verifyCode = "ABC123";

        // When
        String base64Image = ImgVerifyCodeUtils.genVerifyCodeImg(verifyCode);

        // Then
        assertThat(base64Image).isNotNull().isNotEmpty();

        // Optionally display the image
        displayImage(Base64.getDecoder().decode(base64Image), "test_special_chars.jpg");
    }

    @Test
    void testRandomVerifyCodeGeneration() throws IOException {
        // Generate random 6-digit verification code
        String randomCode = ImgVerifyCodeUtils.getRandomVerifyCode(6);
        assertThat(randomCode).hasSize(6).containsOnlyDigits();

        // Generate image for the random code
        String base64Image = ImgVerifyCodeUtils.genVerifyCodeImg(randomCode);
        assertThat(base64Image).isNotNull().isNotEmpty();

        // Display the image
        displayImage(Base64.getDecoder().decode(base64Image), "test_random_code_" + randomCode + ".jpg");
    }

    /**
     * Helper method to display image by writing it to a file
     */
    private void displayImage(byte[] imageBytes, String fileName) throws IOException {
        // Convert byte array back to BufferedImage
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(bis);

        // Write image to file in the project's target directory or user's home
        String outputPath = System.getProperty("user.home") + File.separator + fileName;
        File outputfile = new File(outputPath);
        ImageIO.write(image, "jpg", outputfile);

        System.out.println("Image written to: " + outputPath);
    }
}
