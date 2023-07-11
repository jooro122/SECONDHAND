package com.fullstack.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fullstack.dto.UploadResultDTO;
import com.fullstack.entity.BoardImage;
import com.fullstack.repository.BoardImageRepository;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;

//파일 업로드 처리 컨트롤러..
@RestController
@Log4j2
public class UploadController {

	@Autowired
	BoardImageRepository imageRepository;

	@Value("${com.fullstack.upload}")
	String uploadPath;

	@PostMapping("/uploadAjax")
	// MultipartFile 하는일 : 요청된 업로드 파일을 실제 업로드하는 역할을 하는 객체
	// 사용법은 업로드 요청을 받으려면, 컨트롤러에 요청 패턴을 만들고 --> 처리 메서드 정의하는데
	// 이때 파라미터로 위 객체를 넣어주면 자동으로 프레임워크가 객체를 생성,주입(Injection) 해줍니다.
	public ResponseEntity<List<UploadResultDTO>> uploadFile(MultipartFile[] uploadFiles) {

		// 리턴될 DTO 컬렉션 초기화 작업..
		List<UploadResultDTO> resultDTOList = new ArrayList<>();

		for (MultipartFile uploadFile : uploadFiles) {
			String orgName = uploadFile.getOriginalFilename();
			String fileName = orgName.substring(orgName.lastIndexOf("\\") + 1);

			log.info("원래 파일명 및 패스 : " + orgName);
			log.info("파일이름 : " + fileName);

			// 이미지만 업로드 하도록 제어합니다.
			// header 정보의 contentType 에는 전송되는 데이터 타입이 있습니다.
			// 이를 근거로 업로드 파일의 타입을 조건 분리해서 image가 아닌 경우엔
			// error 코드를 전송시킵니다.
			if (uploadFile.getContentType().startsWith("image") == false) {
				log.warn("이미지 파일이 아닙니다.");
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

			// 업로드 될 폴더 경로를 얻어옵니다.
			String folderPath = makeFolder();

			// UUID를 이용한 Unique File Name 생성.
			String uuid = UUID.randomUUID().toString();

			String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + "_" + fileName;

			// 위 saveName 을 MultipartFile가 수행하는 transterTo()에 맞게 File or Path객체로 변환해줘야 합니다.
			// 아래는 그 작업수행..
			Path savePath = Paths.get(saveName);

			// 업로드 처리.
			try {
				uploadFile.transferTo(savePath);
				log.warn("--------------------파일 업로드 성공-----------------------");

				// 썸네일 이미지 생성..

				String thumbNailName = uploadPath + File.separator + folderPath + File.separator + "s_" + uuid + "_"
						+ fileName;

				File thumbFile = new File(thumbNailName);

				// 추가된 썸네일 이미지 생성 API 응용해서 이미지 생성하기
				Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 100, 100);

				resultDTOList.add(new UploadResultDTO(fileName, uuid, folderPath));

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}

		}
		return new ResponseEntity<>(resultDTOList, HttpStatus.OK);
	}

	// 이미지 처리용 메서드 정의합니다.

	/*
	 * 메서드의 주요 내용은 아래와 같음.. 1. 사용자에게 ajax 의 응답으로 이미지의 경로 등을 보냅니다. 2. 사용자는 해당 경로에 따른
	 * 이미지를 보게 되는데, 이때 실제 이미지가 필요합니다. 3. 이미지는 사용자가 받아서 볼 수 있는 형태로 보내져야 하는데, 자바에서
	 * 문자열을 제외한 모든 데이터는 byte 형태로 보내집니다.(사실 문자열도 내부적으로는 byte로 encdoing,decoding되어 질뿐)
	 * 
	 * 해서 해당 이미지를 byte[]로 보내는 작업도 병행합니다.
	 */

	@GetMapping("/display")
	public ResponseEntity<byte[]> getFile(String fileName, String size) {
		ResponseEntity<byte[]> result = null;

		try {
			String sourceFileName = URLDecoder.decode(fileName, "UTF-8");

			log.info("요청된 파일명 : " + sourceFileName);

			// 요청된 파일을 찾아서 File 객체화합니다
			// 모든 파일은 Root(C:\\upload)하위에 존재하고 , 파일이름은 path 경로와 같이 되어있으니, 두개를 묶어서 생성합니다.

			File file = new File(uploadPath + File.separator + sourceFileName);

			if (size != null && size.equals("1")) {
				file = new File(file.getParent(), file.getName());
			}

			log.info("찾아낸 파일 : " + file.getName() + file.getAbsolutePath());

			// 사용자에게 전송할 HTTPHeader 준비.. 스프링부트에서 객체로 제공함.
			// 내부적으로는 Servlet의 HttpHeader 객체를 확장한것임.

			HttpHeaders header = new HttpHeaders();

			// Mime type 설정
			header.add("Content-type", Files.probeContentType(file.toPath()));
			result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}

	@Transactional
	@PostMapping("/removeFile")
	public ResponseEntity<Boolean> removeFile(String fileName) {
		if (fileName == null) {
			return ResponseEntity.badRequest().build();
		}
		String srcFileName = null;
		try {

			srcFileName = URLDecoder.decode(fileName, "UTF-8");

			// DB에서 이미지 삭제
			String[] uuidAndImgName = srcFileName.split("_", 2);

			if (uuidAndImgName.length == 2) {
				String uuid = uuidAndImgName[0].substring(11);// 11은 yyyy\\MM\\dd/ 다음 문자열의 인덱스(없으면 날짜값도 같이나옴)
				imageRepository.deleteByUuid(uuid);
			}
			// DB에서 삭제 끝

			File file = new File(uploadPath + File.separator + srcFileName);
			boolean result = file.delete();

			File thumbnail = new File(file.getParent(), "s_" + file.getName());
			result = thumbnail.delete();

			// 기본 이미지 파일 삭제
			if (!result && fileName.startsWith("s_")) {
				String orgFileName = fileName.substring(2);
				File orgFile = new File(uploadPath + File.separator + orgFileName);
				orgFile.delete();
			}

			return new ResponseEntity<>(result, HttpStatus.OK);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 아래 메서드는 요청자의 파일을 일자별로 구분지어서, 저장되는 경로를 년/월/일 구분 후 해당 일에 저장하도록 정의 합니다.
	// 또한, 파일명을 UUID를 이용해서 변경하는 작업또한 작성합니다.
	private String makeFolder() {
		String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

		// 저장되는 파일 경로를 /--> 플랫폼에 종속적인 경로 패스로 변경
		String folderPath = str.replace("/", File.separator);

		File uploadPathFolder = new File(uploadPath, folderPath);

		System.out.println("업로드된 파일패스 --> " + uploadPath);

		// mkDirs() 를 호출해서 지정된 패스 경로에 맞게 동적으로 폴더 생성합니다.
		if (!uploadPathFolder.exists()) {
			System.err.println("생성된 폴더 --> " + uploadPathFolder.mkdirs());
		}

		return folderPath;

	}
}
