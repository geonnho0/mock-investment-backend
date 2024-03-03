package org.mockInvestment.util;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockInvestment.stock.controller.StockController;
import org.mockInvestment.stock.service.StockService;
import org.mockInvestment.stock.util.PeriodExtractor;
import org.mockInvestment.support.AuthFilter;
import org.mockInvestment.support.auth.AuthenticationPrincipalArgumentResolver;
import org.mockInvestment.support.token.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest({
        StockController.class
})
@WithMockUser
@ExtendWith(RestDocumentationExtension.class)
public class ControllerTest {

    protected MockMvcRequestSpecification restDocs;

    @MockBean
    protected StockService stockService;

    @MockBean
    protected AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver;

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    @MockBean
    protected AuthFilter authFilter;

    @MockBean
    @Qualifier("oneWeekPeriodExtractor")
    protected PeriodExtractor oneWeekPeriodExtractor;

    @MockBean
    @Qualifier("threeMonthsPeriodExtractor")
    protected PeriodExtractor threeMonthsPeriodExtractor;

    @MockBean
    @Qualifier("oneYearPeriodExtractor")
    protected PeriodExtractor oneYearPeriodExtractor;

    @MockBean
    @Qualifier("fiveYearsPeriodExtractor")
    protected PeriodExtractor fiveYearsPeriodExtractor;


    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        restDocs = RestAssuredMockMvc.given()
                .mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext)
                        .apply(springSecurity())
                        .defaultRequest(post("/**").with(csrf().asHeader()))
                        .defaultRequest(delete("/**").with(csrf().asHeader()))
                        .defaultRequest(put("/**").with(csrf().asHeader()))
                        .apply(documentationConfiguration(restDocumentation)
                                .operationPreprocessors()
                                .withRequestDefaults(prettyPrint(), modifyHeaders().remove("X-CSRF-TOKEN"))
                                .withResponseDefaults(prettyPrint(), modifyHeaders()
                                        .remove("X-Content-Type-Options")
                                        .remove("X-XSS-Protection")
                                        .remove("Pragma")
                                        .remove("X-Frame-Options")))
                        .build())
                .log().all();
    }

}
