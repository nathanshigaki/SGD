package com.govmt.sgd.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.govmt.sgd.dto.request.UsuarioRequest;
import com.govmt.sgd.dto.response.UsuarioResponse;
import com.govmt.sgd.mappers.UsuarioMapper;
import com.govmt.sgd.model.Usuario;
import com.govmt.sgd.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public UsuarioResponse createUsuario(UsuarioRequest usuarioRequest){
        if (usuarioRequest.nome().strip() == null) throw new IllegalArgumentException("Nome vazio");
        if (usuarioRequest.senha() == null) throw new IllegalArgumentException("Senha vazia");
        
        return usuarioMapper.toResponseFromUsuario(
            usuarioRepository.save(usuarioMapper.toUsuarioFromRequest(usuarioRequest)));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> getAll(){
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponseFromUsuario)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse findById(UUID id){
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toResponseFromUsuario)
                .orElseThrow(() -> new RuntimeException("N encontrado"));
    }

    @Transactional
    public UsuarioResponse updateUsuario(UsuarioRequest usuarioRequest){
        UsuarioResponse usuarioResponse = findById(usuarioRequest.id());

        if (usuarioResponse.nome() == usuarioRequest.nome()) throw new IllegalArgumentException("mesmo nome");

        Usuario usuario = usuarioMapper.toUsuarioFromResponse(usuarioResponse);
        usuario.setNome(usuarioRequest.nome());
        return usuarioMapper.toResponseFromUsuario(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deleteUsuario(UUID id){
        UsuarioResponse usuarioExiste = findById(id);
        usuarioRepository.delete(usuarioMapper.toUsuarioFromResponse(usuarioExiste));
    }
}
