package org.tix.backend.dto.mapper;

import org.springframework.stereotype.Component;
import org.tix.backend.dto.AssessorDTO;
import org.tix.backend.model.User;

@Component
public class AssessorMapper {
    public AssessorDTO toAssessorDTO(User user) {
        AssessorDTO assessorDTO = new AssessorDTO();
        assessorDTO.setId(user.getId());
        assessorDTO.setLogin(user.getLogin());
        return assessorDTO;
    }
}
