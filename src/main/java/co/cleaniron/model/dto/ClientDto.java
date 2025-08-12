package co.cleaniron.model.dto;

import java.util.Set;

public record ClientDto(String document, String typeId, String name, String surname,
                        String phone, String email, Set<AddressDto> addresses) {
}