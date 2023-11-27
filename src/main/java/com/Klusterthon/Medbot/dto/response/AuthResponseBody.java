package com.Klusterthon.Medbot.dto.response;

import com.Klusterthon.Medbot.model.Role;
import com.Klusterthon.Medbot.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseBody {
    private String accessToken;
    private String userRole;
    private User user;
}
