package com.project.bookstudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.study_group.api.controller.StudyGroupController;
import com.project.bookstudy.study_group.api.service.StudyGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = {
        StudyGroupController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected StudyGroupService studyGroupService;

}
