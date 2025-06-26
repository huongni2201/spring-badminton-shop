package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.Role;
import badminton_shop.badminton.domain.User;
import badminton_shop.badminton.domain.dto.*;
import badminton_shop.badminton.domain.response.ResCreateUserDTO;
import badminton_shop.badminton.domain.response.ResUpdateUserDTO;
import badminton_shop.badminton.domain.response.ResUserDTO;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.utils.enums.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import badminton_shop.badminton.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {
	private final UserRepository userRepository;
	private final RoleService roleService;

	public UserService(UserRepository userRepository, RoleService roleService) {
		this.roleService = roleService;
		this.userRepository = userRepository;
	}

	public ResultPaginationDTO fetchUsers(Specification<User> spec, Pageable pageable) {
		Page<User> users = this.userRepository.findAll(spec, pageable);
		ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
		ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

		meta.setPage(pageable.getPageNumber() + 1);
		meta.setPageSize(pageable.getPageSize());
		meta.setTotalPages(users.getTotalPages());
		meta.setTotalItems(users.getTotalElements());

		// remove sensitive data
		List<ResUserDTO> resUsers = users.getContent().stream().map(this::convertToResUserDTO).toList();

		resultPaginationDTO.setMeta(meta);
		resultPaginationDTO.setResult(resUsers);

		return resultPaginationDTO;
	}

	public User fetchUserById(Long id) {
		Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

	public User handleUpdateUser(User reqUser) {
		User currentUser = this.fetchUserById(reqUser.getUserId());

		if (currentUser != null) {
			currentUser.setFullName(reqUser.getFullName());
			currentUser.setAddress(reqUser.getAddress());
			currentUser.setPhone(reqUser.getPhone());
			currentUser.setGender(reqUser.getGender());
			currentUser.setAvatar(reqUser.getAvatar());
			currentUser.setDob(reqUser.getDob());
			return userRepository.save(currentUser);
		}
		return currentUser;
	}

	public void updateUserToken(String token, String email) {
		 User currentUser = this.userRepository.findByEmail(email);
		if (currentUser != null) {
			currentUser.setRefreshToken(token);
			this.userRepository.save(currentUser);
		}
	}

	public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
		return userRepository.findByRefreshTokenAndEmail(refreshToken, email);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public boolean isEmailExist(String email) {
		return userRepository.existsByEmail(email);
	}

	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}

	public User handleCreateUser(User user) {
		if (user.getRole() != null) {
			Optional<Role> role = Optional.ofNullable(roleService.findByName(user.getRole().getName()));
			user.setRole(role.orElseGet(() -> roleService.findByName(RoleName.USER)));
		}
		return userRepository.save(user);
	}

	public ResCreateUserDTO convertToResCreateUserDTO(User user) {
		ResCreateUserDTO.RoleUser roleUser = new ResCreateUserDTO.RoleUser();

		ResCreateUserDTO res = new ResCreateUserDTO();
		res.setId(user.getUserId());
		res.setFullName(user.getFullName());
		res.setEmail(user.getEmail());
		res.setPhone(user.getPhone());

		if (user.getRole() != null) {
			roleUser.setRole(String.valueOf(user.getRole().getName()));
			roleUser.setId(user.getRole().getId());
			res.setRole(roleUser);
		}

		res.setAddress(user.getAddress());
		res.setAvatar(user.getAvatar());
		res.setDob(user.getDob());
		res.setGender(user.getGender());
		res.setCreatedAt(user.getCreatedAt());

		return res;
	}

	public ResUserDTO convertToResUserDTO(User user) {
		ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
		ResUserDTO res = new ResUserDTO();
		res.setId(user.getUserId());
		res.setFullName(user.getFullName());
		res.setEmail(user.getEmail());
		res.setPhone(user.getPhone());

		if (user.getRole() != null) {
			roleUser.setRole(String.valueOf(user.getRole().getName()));
			roleUser.setId(user.getRole().getId());
			res.setRole(roleUser);
		}

		res.setAddress(user.getAddress());
		res.setAvatar(user.getAvatar());
		res.setDob(user.getDob());
		res.setGender(user.getGender());
		res.setCreatedAt(user.getCreatedAt());
		res.setUpdatedAt(user.getUpdatedAt());

		return res;
	}

	public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
		ResUpdateUserDTO.RoleUser roleUser = new ResUpdateUserDTO.RoleUser();

		ResUpdateUserDTO res = new ResUpdateUserDTO();
		res.setId(user.getUserId());
		res.setFullName(user.getFullName());
		res.setPhone(user.getPhone());

		if (user.getRole() != null) {
			roleUser.setRole(String.valueOf(user.getRole().getName()));
			roleUser.setId(user.getRole().getId());
			res.setRole(roleUser);
		}
		res.setAddress(user.getAddress());
		res.setAvatar(user.getAvatar());
		res.setDob(user.getDob());
		res.setGender(user.getGender());
		res.setUpdatedAt(user.getUpdatedAt());

		return res;
	}
}
