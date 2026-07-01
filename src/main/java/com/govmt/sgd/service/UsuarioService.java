package com.govmt.sgd.service;

import org.springframework.stereotype.Service;

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

    public UsuarioResponse createUsuario(UsuarioRequest usuarioRequest){
        if (usuarioRequest.nome().strip() == null){
            throw new IllegalArgumentException("Nome vazio");
        }

        if (usuarioRequest.senha() == null){
            throw new IllegalArgumentException("Senha vazia");
        }

        Usuario usuarioSalvar = usuarioMapper.toUsuarioFromRequest(usuarioRequest);
        Usuario usuarioSalvado = usuarioRepository.save(usuarioSalvar);
        return usuarioMapper.toResponseFromUsuario(usuarioSalvado);
    }
}
