package co.cleaniron.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ServiceDto {
    @EqualsAndHashCode.Include
    private Integer idService;

    @EqualsAndHashCode.Include
    private String serviceDescription;

    public ServiceDto(Integer idService, String serviceDescription) {
        this.idService = idService;
        this.serviceDescription = serviceDescription;
    }
}

