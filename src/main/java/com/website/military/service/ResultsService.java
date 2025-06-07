package com.website.military.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.website.military.domain.Entity.Results;
import com.website.military.domain.dto.response.ResponseDataDto;
import com.website.military.domain.dto.response.ResponseMessageDto;
import com.website.military.domain.dto.results.response.GetGradingResponse;
import com.website.military.repository.ResultsRepository;

import jakarta.servlet.http.HttpServletRequest;


@Service
public class ResultsService {
    @Value("${error.INTERNAL_SERVER_ERROR}")
    private String internalError;

    @Value("${error.BAD_REQUEST_ERROR}")
    private String badRequestError;

    @Value("${error.UNAUTHORIZE}")
    private String unAuthorize;

    @Autowired
    private ResultsRepository resultsRepository;

    @Autowired
    private AuthService authService;

    public ResponseEntity<?> getGradingResult(HttpServletRequest request, Long resultId){
        Long userId = authService.getUserId(request);
        Optional<Results> existingResults = resultsRepository.findByUser_UserIdAndResultId(userId, resultId);
        if(existingResults.isPresent()){
            Results result = existingResults.get();
            GetGradingResponse response = new GetGradingResponse(result.getResultId(), result.getScore(), result.getSubmittedAt());
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",response));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
    }

    public ResponseEntity<?> getAllGradingResult(HttpServletRequest request){
        Long userId = authService.getUserId(request);
        List<Results> existingResults = resultsRepository.findByUser_UserId(userId);
        if(existingResults.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessageDto.set(badRequestError, "잘못된 요청입니다."));
        }else{
            List<GetGradingResponse> responses = new ArrayList<>();
            for(Results result : existingResults){
                GetGradingResponse response = new GetGradingResponse(result.getResultId(), result.getScore(), result.getSubmittedAt());
                responses.add(response);
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDataDto.set("OK",responses));
        }
    }
}
