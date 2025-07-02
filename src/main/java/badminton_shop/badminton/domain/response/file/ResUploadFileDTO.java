package badminton_shop.badminton.domain.response.file;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class ResUploadFileDTO {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
    private Instant uploadedAt;
}
