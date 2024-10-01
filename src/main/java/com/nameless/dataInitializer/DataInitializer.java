package com.nameless.dataInitializer;

import com.nameless.entity.question.model.Question;
import com.nameless.entity.question.repository.QuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final QuestionRepository questionRepository;

    public DataInitializer(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if questions already exist to avoid duplicates
        if (questionRepository.count() == 0) {
            Question question1 = Question.builder()
                    .title("Hello World Program")
                    .description("Write a program to print 'Hello, World!'")
                    .expectedOutput("Hello, World!")
                    .testCase("print('Hello, World!')")
                    .build();
            Question question2 = Question.builder()
                    .title("Sum of Two Numbers")
                    .description("Write a program to sum two numbers.")
                    .expectedOutput("5")
                    .testCase("a = 2\nb = 3\nprint(a + b)")
                    .build();

            // Save initial questions to the database
            questionRepository.save(question1);
            questionRepository.save(question2);
        }
    }
}