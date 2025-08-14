package com.example.datn.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ChatAiRequest {
    private String prompt;
    private List<ChatAiMessage> history;
}


