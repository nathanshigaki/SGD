package com.govmt.sgd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.govmt.sgd.dto.response.HistoricoResponse;
import com.govmt.sgd.mappers.HistoricoMapper;
import com.govmt.sgd.model.Documento;
import com.govmt.sgd.model.Historico;
import com.govmt.sgd.model.Usuario;
import com.govmt.sgd.repository.HistoricoRepository;
import com.govmt.sgd.service.HistoricoService;

@ExtendWith(MockitoExtension.class)
@DisplayName("HistoricoService")
class HistoricoServiceTest {

    @InjectMocks
    private HistoricoService historicoService;

    @Mock
    private HistoricoRepository historicoRepository;

    @Mock
    private HistoricoMapper historicoMapper;

    private Documento documentoMock;
    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        documentoMock = new Documento();
        documentoMock.setId(UUID.randomUUID());

        usuarioMock = new Usuario();
        usuarioMock.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("should save historico correctly to repository")
    void shouldSaveHistoricoCorrectlyToRepository() {
        historicoService.saveHistorico(
            documentoMock, usuarioMock, usuarioMock, "APROVADO", "ATUALIZAR_DOCUMENTO", "Antes", "Depois"
        );

        verify(historicoRepository).save(any(Historico.class));
    }

    @Test
    @DisplayName("should create solicitation with pending status")
    void shouldCreateSolicitationWithPendingStatus() {
        Historico historicoSalvo = new Historico();
        historicoSalvo.setSituacao("PENDENTE_APROVACAO");
        when(historicoRepository.save(any(Historico.class))).thenReturn(historicoSalvo);
        
        HistoricoResponse responseMock = new HistoricoResponse(
            UUID.randomUUID(), null, null, null, "PENDENTE_APROVACAO", "CRIAR_DOCUMENTO", null, null
        );
        when(historicoMapper.toResponseFromHistorico(historicoSalvo)).thenReturn(responseMock);

        HistoricoResponse result = historicoService.solicitarAprovacao(
            documentoMock, usuarioMock, "CRIAR_DOCUMENTO", null, "JSON Depois"
        );

        assertNotNull(result);
        assertEquals("PENDENTE_APROVACAO", result.situacao());
        verify(historicoRepository).save(any(Historico.class));
    }

    @Test
    @DisplayName("should return paginated historico list excluding pending ones")
    void shouldReturnPaginatedHistoricoListExcludingPendingOnes() {
        Pageable pageable = PageRequest.of(0, 10);
        Historico historico = new Historico();
        Page<Historico> pageMock = new PageImpl<>(List.of(historico));

        when(historicoRepository.getAll(pageable)).thenReturn(pageMock);

        Page<HistoricoResponse> result = historicoService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(historicoRepository).getAll(pageable);
    }
}