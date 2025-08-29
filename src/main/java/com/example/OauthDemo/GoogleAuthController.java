package com.example.OauthDemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/google")
@Slf4j
public class GoogleAuthController {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    String tokenEndpoint="https://oauth2.googleapis.com/token";


    //Google Callback Function
    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code){

        //Exchange Authcode for tokens
        try{
            MultiValueMap<String,String> parms=new LinkedMultiValueMap<>();

            parms.add("code",code);
            parms.add("client_id",clientId);
            parms.add("client_secret",clientSecret);
            parms.add("redirect_uri","https://developers.google.com/oauthplayground");
            parms.add("grant_type","authorization_code");

            HttpHeaders headers=new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request=new HttpEntity<>(parms,headers);

            ResponseEntity<Map> tokenResponse=restTemplate.postForEntity(tokenEndpoint,request, Map.class);

            String idToken=(String)tokenResponse.getBody().get("id_token");

            String userInfo="https://oauth2.googleapis.com/tokeninfo?id_token="+idToken;

            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfo, Map.class);

            if(userInfoResponse.getStatusCode()== HttpStatus.OK){
                Map<String,Object> userDetails=userInfoResponse.getBody();
                String email=(String)userDetails.get("email");
                log.info("Logged in user Email id is: "+email);
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            log.info("Authentication completed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();


        }
        catch(Exception e){
            log.error("Exception occured"+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }





    }



}
