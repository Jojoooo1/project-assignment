package com.project.api.controllers.customer;

import com.project.api.constants.AppUrls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(CustomerController.BASE_URL)
@RequiredArgsConstructor
public class CustomerController {

  public static final String BASE_URL = AppUrls.CUSTOMER_API;

  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public String helloWorld() {
    return "hello world";
  }


}
