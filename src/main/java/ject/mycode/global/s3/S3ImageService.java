package ject.mycode.global.s3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3ImageService {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucketName}")
	private String bucketName;

	public String upload(MultipartFile image) {
		if (image == null || image.isEmpty() || image.getOriginalFilename() == null) {
			throw new IllegalArgumentException("이미지 파일이 비어 있거나 파일명이 없습니다.");
		}
		return this.uploadImage(image);
	}

	private String uploadImage(MultipartFile image) {
		String originalFilename = image.getOriginalFilename();
		validateImageFileExtension(originalFilename);

		try {
			return uploadImageToS3(image, originalFilename);
		} catch (IOException e) {
			// 체크 예외를 런타임으로 전환하여 컨트롤러까지 전파
			throw new UncheckedIOException("이미지 업로드 중 I/O 오류가 발생했습니다.", e);
		}
	}

	private void validateImageFileExtension(String filename) {
		int lastDotIndex = filename.lastIndexOf('.');
		if (lastDotIndex < 0) {
			throw new IllegalArgumentException("확장자가 없습니다.");
		}
		String ext = filename.substring(lastDotIndex + 1).toLowerCase(Locale.ROOT);
		List<String> allowed = Arrays.asList("jpg", "jpeg", "png", "gif");
		if (!allowed.contains(ext)) {
			throw new IllegalArgumentException("허용되지 않은 확장자입니다: " + ext);
		}
	}

	private String uploadImageToS3(MultipartFile image, String originalFilename) throws IOException {
		int lastDotIndex = originalFilename.lastIndexOf('.');
		String ext = originalFilename.substring(lastDotIndex + 1).toLowerCase(Locale.ROOT);

		// 원본명 노출을 피하고, 충돌 방지를 위해 UUID만 사용
		String s3FileName = UUID.randomUUID().toString() + "." + ext;

		byte[] bytes = image.getBytes();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType("image/" + ext);       // 예: image/jpeg
		metadata.setContentLength(bytes.length);

		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
			PutObjectRequest putObjectRequest =
				new PutObjectRequest(bucketName, s3FileName, bais, metadata)
					.withCannedAcl(CannedAccessControlList.PublicRead); // 버킷 공개전략에 맞게 조정
			amazonS3.putObject(putObjectRequest); // 실패 시 AmazonServiceException/SdkClientException 전파
		}

		return amazonS3.getUrl(bucketName, s3FileName).toString();
	}

	public void deleteImageFromS3(String imageAddress) {
		String key = getKeyFromImageAddress(imageAddress);
		// 실패 시 AmazonServiceException/SdkClientException 전파
		amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
	}

	private String getKeyFromImageAddress(String imageAddress) {
		try {
			URL url = new URL(imageAddress);
			String decoded = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
			if (decoded.length() <= 1) {
				throw new IllegalArgumentException("유효하지 않은 S3 URL입니다.");
			}
			return decoded.substring(1); // 선행 '/' 제거
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("잘못된 URL 형식입니다: " + imageAddress, e);
		}
	}
}
