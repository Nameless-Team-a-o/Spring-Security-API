package com.nameless.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameless.dto.SubmissionRequestDTO;
import com.nameless.dto.JudgeRequestDTO;
import com.nameless.entity.question.model.Question;
import com.nameless.entity.question.repository.QuestionRepository;
import com.nameless.entity.submission.model.Submission;
import com.nameless.entity.submission.repository.SubmissionRepository;
import com.nameless.entity.user.repository.UserRepository;
import com.nameless.entity.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class SubmissionProducer {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public boolean createSubmission(SubmissionRequestDTO request) {
        User user = findUser(request.getUserId());
        if (user == null) return false;

        Question question = findQuestion(request.getQuestionId());
        if (question == null) return false;

        Submission submission = createAndSaveSubmission(request, user, question);
        if (submission == null) return false;

        sendSubmissionToQueue(request, question, submission);
        return true;
    }

    private User findUser(Long userId) {
        try {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
        } catch (EntityNotFoundException e) {
            logError(e.getMessage());
            return null;
        }
    }

    private Question findQuestion(Long questionId) {
        try {
            return questionRepository.findById(questionId)
                    .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        } catch (EntityNotFoundException e) {
            logError(e.getMessage());
            return null;
        }
    }

    private Submission createAndSaveSubmission(SubmissionRequestDTO request, User user, Question question) {

        try {
            Submission submission = Submission.builder()
                    .user(user)
                    .question(question)
                    .code(request.getCode())
                    .expectedOutput(question.getExpectedOutput())
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .build();
            return submissionRepository.save(submission);
        } catch (Exception e) {
            logError("Error saving submission: " + e.getMessage());
            return null;
        }
    }

    private void sendSubmissionToQueue(SubmissionRequestDTO request, Question question, Submission submission) {
        JudgeRequestDTO judgeRequest = new JudgeRequestDTO(
                submission.getId(),
                request.getLanguageId(),
                request.getCode(),
                question.getExpectedOutput(),
                question.getTestCase());

        try {
            String submissionJson = objectMapper.writeValueAsString(judgeRequest);
            rabbitTemplate.convertAndSend("submissionQueue", submissionJson);
        } catch (JsonProcessingException e) {
            logError("Error serializing submission object: " + e.getMessage());
        } catch (Exception e) {
            logError("Error sending message to RabbitMQ: " + e.getMessage());
        }
    }

    private void logError(String message) {
        log.error(message);
    }
}
