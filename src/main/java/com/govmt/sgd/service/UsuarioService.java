package com.govmt.sgd.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.govmt.sgd.dto.request.UsuarioRequest;
import com.govmt.sgd.dto.response.UsuarioResponse;
import com.govmt.sgd.exception.InvalidArgumentException;
import com.govmt.sgd.exception.NotFoundException;
import com.govmt.sgd.mappers.UsuarioMapper;
import com.govmt.sgd.model.UserAuthenticated;
import com.govmt.sgd.model.Usuario;
import com.govmt.sgd.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService{

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse createUsuario(UsuarioRequest usuarioRequest){
        if (usuarioRepository.findByEmail(usuarioRequest.email()).isPresent()) {
            throw new InvalidArgumentException("Este e-mail já está cadastrado no sistema.");
        }
        
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

    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado")); //mudar excecao para notauthenticated

        return new UserAuthenticated(usuario);
    }

}
