package com.splitmart.application.command;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OptionGroupCommand {
    
    private String name;
    private List<OptionCommand> options;
} 