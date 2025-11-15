package com.example.springbootstarterkit.apis.user;

import com.example.springbootstarterkit.apis.memo.MemoEntity;
import com.example.springbootstarterkit.apis.memo.MemoRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final MemoRepository memoRepository;

  public CustomUserDetailsService(
    UserRepository userRepository,
    PasswordEncoder passwordEncoder,
    MemoRepository memoRepository
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.memoRepository = memoRepository;
  }

  @Override
  public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity user = userRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return new CustomUserDetails(user);
  }

  @Transactional
  public void register(String username, String rawPassword) {
    String enc = passwordEncoder.encode(rawPassword);
    UserEntity u = new UserEntity();
    u.setUsername(username);
    u.setPassword(enc);
    u.setRole("USER");
    UserEntity savedUser = userRepository.save(u);

    // 회원가입 환영 메모 자동 생성
    MemoEntity welcomeMemo = new MemoEntity();
    welcomeMemo.setUserId(savedUser.getId());
    welcomeMemo.setContent("회원가입을 환영합니다");
    memoRepository.save(welcomeMemo);
  }

}
