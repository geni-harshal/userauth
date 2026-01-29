package com.example.userauth.controller;

import com.example.userauth.dto.CompanyCreateRequest;
import com.example.userauth.entity.Company;
import com.example.userauth.repository.CompanyRepository;
import com.example.userauth.service.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
public class CompanyUploadController {

    private final CompanyService companyService;
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${app.uploads.dir:uploads}")
    private String uploadDir;

    @Value("${app.python.analyze.url:http://localhost:8000/analyze}")
    private String pythonAnalyzeUrl;

    public CompanyUploadController(
            CompanyService companyService,
            CompanyRepository companyRepository,
            ObjectMapper objectMapper,
            RestTemplate restTemplate
    ) {
        this.companyService = companyService;
        this.companyRepository = companyRepository;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAndAnalyze(
            @RequestPart("company") String companyJson,
            @RequestPart("file") MultipartFile file,
            HttpServletRequest request
    ) {
        try {
            /* ============================
               1️⃣ AUTH USER (FROM FILTER)
               ============================ */
            Object authUser = request.getAttribute("authUser");
            if (authUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Unauthorized");
            }

            Long userId =
                    Long.parseLong(authUser.toString().split("\\|")[0]);

            /* ============================
               2️⃣ PARSE COMPANY JSON
               ============================ */
            CompanyCreateRequest req =
                    objectMapper.readValue(companyJson, CompanyCreateRequest.class);

            /* ============================
               3️⃣ CREATE COMPANY (SERVICE)
               ============================ */
            Company company =
                    companyService.createCompany(req, userId);

            /* ============================
               4️⃣ SAVE PDF
               ============================ */
            Files.createDirectories(Paths.get(uploadDir));
            String filename =
                    company.getId() + "_" + file.getOriginalFilename();
            Path pdfPath =
                    Paths.get(uploadDir, filename);
            Files.write(pdfPath, file.getBytes());

            /* ============================
               5️⃣ CALL PYTHON AI
               ============================ */
            MultiValueMap<String, Object> body =
                    new LinkedMultiValueMap<>();

            ByteArrayResource resource =
                    new ByteArrayResource(file.getBytes()) {
                        @Override
                        public String getFilename() {
                            return file.getOriginalFilename();
                        }
                    };

            body.add("file", resource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> pythonRequest =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> aiResponse =
                    restTemplate.postForEntity(
                            pythonAnalyzeUrl,
                            pythonRequest,
                            Map.class
                    );

            /* ============================
               6️⃣ SAVE ANALYSIS RESULT
               ============================ */
            company.setPdfPath(pdfPath.toString());
            company.setAnalysisJson(
                    objectMapper.writeValueAsString(aiResponse.getBody())
            );
            company.setAnalyzedAt(LocalDateTime.now());

            companyRepository.save(company);

            return ResponseEntity.ok(company);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload & analysis failed");
        }
    }
}
