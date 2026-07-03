package com.govmt.sgd.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.govmt.sgd.dto.request.UsuarioRequest;
import com.govmt.sgd.dto.response.UsuarioResponse;
import com.govmt.sgd.exception.NotFoundException;
import com.govmt.sgd.mappers.UsuarioMapper;
import com.govmt.sgd.model.Usuario;
import com.govmt.sgd.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse createUsuario(UsuarioRequest usuarioRequest){
        Usuario usuario = usuarioMapper.toUsuarioFromRequest(usuarioRequest);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioMapper.toResponseFromUsuario(usuarioRepository.save(usuario));
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
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public UsuarioResponse updateUsuario(UsuarioRequest usuarioRequest){
        Usuario usuario = usuarioRepository.findById(usuarioRequest.id())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        usuarioMapper.updateUsuarioFromRequest(usuarioRequest, usuario);
        return usuarioMapper.toResponseFromUsuario(usuario);
    }

    @Transactional
    public void deleteUsuario(UUID id){
        Usuario usuarioExiste = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        usuarioRepository.delete(usuarioExiste);
    }
}
