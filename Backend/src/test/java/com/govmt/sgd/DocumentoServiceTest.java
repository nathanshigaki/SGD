package com.govmt.sgd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.govmt.sgd.dto.ValoresHistorico;
import com.govmt.sgd.dto.request.DocumentoRequest;
import com.govmt.sgd.dto.response.DocumentoResponse;
import com.govmt.sgd.exception.InvalidArgumentException;
import com.govmt.sgd.exception.NotFoundException;
import com.govmt.sgd.mappers.DocumentoMapper;
import com.govmt.sgd.model.Documento;
import com.govmt.sgd.model.Historico;
import com.govmt.sgd.model.Usuario;
import com.govmt.sgd.repository.DocumentoRepository;
import com.govmt.sgd.repository.HistoricoRepository;
import com.govmt.sgd.service.DocumentoService;
import com.govmt.sgd.service.HistoricoService;
import com.govmt.sgd.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentoService")
class DocumentoServiceTest {

    @InjectMocks
    private DocumentoService documentoService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private HistoricoService historicoService;

    @Mock
    private HistoricoRepository historicoRepository;

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private DocumentoMapper documentoMapper;

    @Mock
    private ObjectMapper objectMapper;

    private Usuario usuarioAdmin;
    private Usuario usuarioComum;
    private DocumentoRequest requestMock;
    private Documento documentoMock;
    private DocumentoResponse responseMock;

    @BeforeEach
    void setUp() {
        usuarioAdmin = new Usuario();
        usuarioAdmin.setId(UUID.randomUUID());
        usuarioAdmin.setPermissoes(List.of("*:*"));

        usuarioComum = new Usuario();
        usuarioComum.setId(UUID.randomUUID());
        usuarioComum.setPermissoes(List.of("DOCUMENTO:CRIAR"));

        requestMock = new DocumentoRequest(
                null, UUID.randomUUID(), "STI-TEC-2026/00001", null, null, 
                0, BigDecimal.TEN, "EM_ANALISE", null, false, false, 
                "Resumo Teste", null, null, null, null, null, null, null
        );

        documentoMock = new Documento();
        documentoMock.setId(UUID.randomUUID());
        documentoMock.setSigdoc("STI-TEC-2026/00001");

        responseMock = new DocumentoResponse(
                documentoMock.getId(), null, null, "STI-TEC-2026/00001", null, 
                null, 0, BigDecimal.TEN, "EM_ANALISE", null, false, false, 
                "Resumo Teste", null, null, null, null, null, null, null
        );
    }

    @Test
    @DisplayName("should create document directly when user is admin")
    void shouldCreateDocumentDirectlyWhenUserIsAdmin() {
        when(usuarioService.getUsuarioLogado()).thenReturn(usuarioAdmin);
        when(documentoMapper.toDocumentoFromRequest(requestMock)).thenReturn(documentoMock);
        when(documentoRepository.save(documentoMock)).thenReturn(documentoMock);
        when(documentoMapper.toResponseFromDocumento(documentoMock)).thenReturn(responseMock);

        DocumentoResponse result = documentoService.createDocumento(requestMock);

        assertNotNull(result.id());
        assertEquals("STI-TEC-2026/00001", result.sigdoc());
        
        verify(documentoRepository).save(documentoMock);
        verify(historicoService, never()).solicitarAprovacao(any(), any(), anyString(), any(), any());
    }

    @Test
    @DisplayName("should create pending solicitation when user is not admin")
    void shouldCreatePendingSolicitationWhenUserIsNotAdmin() {
        when(usuarioService.getUsuarioLogado()).thenReturn(usuarioComum);
        when(documentoMapper.toDocumentoFromRequest(requestMock)).thenReturn(documentoMock);
        when(documentoMapper.toResponseFromDocumento(documentoMock)).thenReturn(responseMock);

        DocumentoResponse result = documentoService.createDocumento(requestMock);

        assertNotNull(result);
        verify(documentoRepository, never()).save(any());
        verify(historicoService).solicitarAprovacao(
            eq(documentoMock), eq(usuarioComum), eq("CRIAR_DOCUMENTO"), isNull(), eq(requestMock)
        );
    }

    @Test
    @DisplayName("should return paginated document list")
    void shouldReturnPaginatedDocumentList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Documento> pageMock = new PageImpl<>(List.of(documentoMock));

        when(documentoRepository.findAllWithResponsaveis(pageable)).thenReturn(pageMock);
        when(documentoMapper.toResponseFromDocumento(documentoMock)).thenReturn(responseMock);

        Page<DocumentoResponse> result = documentoService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(documentoRepository).findAllWithResponsaveis(pageable);
    }

    @Test
    @DisplayName("should throw exception when document is not found")
    void shouldThrowExceptionWhenDocumentIsNotFound() {
        UUID invalidId = UUID.randomUUID();
        when(documentoRepository.findById(invalidId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            documentoService.findById(invalidId);
        });

        assertEquals("Documento não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("should update document directly and generate log when user is admin")
    void shouldUpdateDocumentDirectlyAndGenerateLogWhenUserIsAdmin() {
        when(documentoRepository.findById(requestMock.id())).thenReturn(Optional.of(documentoMock));
        when(usuarioService.getUsuarioLogado()).thenReturn(usuarioAdmin);
        when(documentoMapper.toResponseFromDocumento(documentoMock)).thenReturn(responseMock);
        when(documentoRepository.save(documentoMock)).thenReturn(documentoMock);

        DocumentoResponse result = documentoService.updateDocumento(requestMock);

        assertNotNull(result);
        verify(documentoRepository).save(documentoMock);
        verify(historicoService).saveHistorico(
            eq(documentoMock), eq(usuarioAdmin), eq(usuarioAdmin), eq("APROVADO"), eq("ATUALIZAR_DOCUMENTO"), any(), any()
        );
    }

    @Test
    @DisplayName("should generate pending solicitation for update when user is not admin")
    void shouldGeneratePendingSolicitationForUpdateWhenUserIsNotAdmin() {
        when(documentoRepository.findById(requestMock.id())).thenReturn(Optional.of(documentoMock));
        when(usuarioService.getUsuarioLogado()).thenReturn(usuarioComum);
        when(documentoMapper.toResponseFromDocumento(documentoMock)).thenReturn(responseMock);

        DocumentoResponse result = documentoService.updateDocumento(requestMock);

        assertNotNull(result);
        verify(documentoRepository, never()).save(any());
        verify(historicoService).solicitarAprovacao(
            eq(documentoMock), eq(usuarioComum), eq("ATUALIZAR_DOCUMENTO"), any(), eq(requestMock)
        );
    }

    @Test
    @DisplayName("should apply soft delete and generate log when user is admin")
    void shouldApplySoftDeleteAndGenerateLogWhenUserIsAdmin() {
        UUID docId = UUID.randomUUID();
        when(documentoRepository.findById(docId)).thenReturn(Optional.of(documentoMock));
        when(usuarioService.getUsuarioLogado()).thenReturn(usuarioAdmin);
        when(documentoRepository.save(documentoMock)).thenReturn(documentoMock);

        documentoService.deleteDocumento(docId);

        assertNotNull(documentoMock.getDeletadoEm());
        verify(documentoRepository).save(documentoMock);
        verify(historicoService).saveHistorico(
            eq(documentoMock), eq(usuarioAdmin), eq(usuarioAdmin), eq("APROVADO"), eq("DELETAR_DOCUMENTO"), any(), any()
        );
    }

    @Test
    @DisplayName("should generate pending solicitation for delete when user is not admin")
    void shouldGeneratePendingSolicitationForDeleteWhenUserIsNotAdmin() {
        UUID docId = UUID.randomUUID();
        when(documentoRepository.findById(docId)).thenReturn(Optional.of(documentoMock));
        when(usuarioService.getUsuarioLogado()).thenReturn(usuarioComum);
        when(documentoMapper.toResponseFromDocumento(documentoMock)).thenReturn(responseMock);

        documentoService.deleteDocumento(docId);

        verify(documentoRepository, never()).save(any());
        verify(historicoService).solicitarAprovacao(
            eq(documentoMock), eq(usuarioComum), eq("DELETAR_DOCUMENTO"), eq(responseMock), isNull()
        );
    }

    @Test
    @DisplayName("should save final document when creation solicitation is approved")
    void shouldSaveFinalDocumentWhenCreationSolicitationIsApproved() {
        UUID historicoId = UUID.randomUUID();
        Historico historico = new Historico();
        historico.setSituacao("PENDENTE_APROVACAO");
        historico.setAcao("CRIAR_DOCUMENTO");
        historico.setUsuario(usuarioComum);
        historico.setValores(new ValoresHistorico(null, requestMock));

        when(historicoRepository.findById(historicoId)).thenReturn(Optional.of(historico));
        when(usuarioService.getUsuarioLogado()).thenReturn(usuarioAdmin);
        when(objectMapper.convertValue(historico.getValores().depois(), DocumentoRequest.class)).thenReturn(requestMock);
        when(documentoMapper.toDocumentoFromRequest(requestMock)).thenReturn(documentoMock);
        when(documentoRepository.save(documentoMock)).thenReturn(documentoMock);

        documentoService.validarSolicitacao(historicoId, true);

        assertEquals("APROVADO", historico.getSituacao());
        verify(documentoRepository).save(documentoMock);
        verify(historicoService).saveHistorico(
            eq(documentoMock), eq(usuarioComum), eq(usuarioAdmin), eq("APROVADO"), eq("CRIAR_DOCUMENTO"), any(), any()
        );
    }

    @Test
    @DisplayName("should keep solicitation as rejected when validation is false")
    void shouldKeepSolicitationAsRejectedWhenValidationIsFalse() {
        UUID historicoId = UUID.randomUUID();
        Historico historico = new Historico();
        historico.setSituacao("PENDENTE_APROVACAO");
        historico.setAcao("CRIAR_DOCUMENTO");
        historico.setDocumento(documentoMock);
        historico.setUsuario(usuarioComum);
        historico.setValores(new ValoresHistorico(null, requestMock));

        when(historicoRepository.findById(historicoId)).thenReturn(Optional.of(historico));
        when(usuarioService.getUsuarioLogado()).thenReturn(usuarioAdmin);

        documentoService.validarSolicitacao(historicoId, false);

        verify(documentoRepository, never()).save(any());
        verify(historicoService).saveHistorico(
            eq(documentoMock), eq(usuarioComum), eq(usuarioAdmin), eq("REJEITADO"), eq("CRIAR_DOCUMENTO"), any(), any()
        );
    }

    @Test
    @DisplayName("should throw exception when trying to validate an already processed solicitation")
    void shouldThrowExceptionWhenValidatingAlreadyProcessedSolicitation() {
        UUID historicoId = UUID.randomUUID();
        Historico historico = new Historico();
        historico.setSituacao("APROVADO"); 

        when(historicoRepository.findById(historicoId)).thenReturn(Optional.of(historico));

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> {
            documentoService.validarSolicitacao(historicoId, true);
        });

        assertEquals("Esta solicitação já foi processada anteriormente.", exception.getMessage());
    }

    @Test
    @DisplayName("should throw exception when validation action is unknown")
    void shouldThrowExceptionWhenValidationActionIsUnknown() {
        UUID historicoId = UUID.randomUUID();
        Historico historico = new Historico();
        historico.setSituacao("PENDENTE_APROVACAO");
        historico.setAcao("UNKNOWN_ACTION");

        when(historicoRepository.findById(historicoId)).thenReturn(Optional.of(historico));
        when(usuarioService.getUsuarioLogado()).thenReturn(usuarioAdmin);

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> {
            documentoService.validarSolicitacao(historicoId, true);
        });

        assertEquals("Ação de histórico desconhecida.", exception.getMessage());
    }
}