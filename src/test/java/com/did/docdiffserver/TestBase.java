package com.did.docdiffserver;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = DocDiffServerApplicationTests.class, value = {"spring.main.allow-bean-definition-overriding=true"})
public class TestBase {

}
