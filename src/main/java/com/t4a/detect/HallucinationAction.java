package com.t4a.detect;

import com.t4a.annotations.Action;
import com.t4a.processor.AIProcessor;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * The HallucinationAction class implements the AIAction interface and is designed to detect
 * potential hallucinations within a generative model's responses. It achieves this by asking follow-up
 * questions related to factual consistency between the provided context and the model's generated answer
 */
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class HallucinationAction {
    private AIProcessor processor;
    private String context;


    @Action
    public List<HallucinationQA> askQuestions(String questions[]) {
        List<HallucinationQA> haList = new ArrayList<HallucinationQA>();




            for (String question : questions
            ) {
                try {

                    log.debug(question);
                    String answer = processor.query(question);

                    String truth = processor.query("Look at both paragraphs what % of truth is there in the first paragraph based on the 2nd paragraph, Paragraph 1- "+context+" - Paragraph 2 -"+answer+" - provide your answer in just percentage and nothing else");

                    log.debug(truth);
                    HallucinationQA hallu = new HallucinationQA(question,answer,context,truth);
                    haList.add(hallu);

                }catch (Exception e) {
                    log.error(e.getMessage());
                }

            }

        return haList;
    }


}
