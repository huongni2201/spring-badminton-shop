package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.Role;
import badminton_shop.badminton.domain.User;
import badminton_shop.badminton.domain.response.user.ResCreateUserDTO;
import badminton_shop.badminton.domain.response.user.ResUpdateUserDTO;
import badminton_shop.badminton.domain.response.user.ResUserDTO;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.utils.SecurityUtil;
import badminton_shop.badminton.utils.exception.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import badminton_shop.badminton.repository.UserRepository;

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
		ResultPaginationDTO resultPaginationDTO = ResultPaginationDTO.builder().build();
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
			currentUser.setPhone(reqUser.getPhone());
			currentUser.setGender(reqUser.getGender());
			currentUser.setAvatar(reqUser.getAvatar());
			currentUser.setDob(reqUser.getDob());
			currentUser.setAddress(reqUser.getAddress());

			if (reqUser.getRole() != null) {
				Role foundRole = roleService.fetchRoleByName(reqUser.getRole().getName());
				currentUser.setRole(foundRole);
			}

			currentUser = this.userRepository.save(currentUser);
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

	public User fetchUserLogin() throws IdInvalidException {
		String email = SecurityUtil.getCurrentUserLogin()
				.orElseThrow(() -> new IdInvalidException("User not authenticated!"));

		User currentUser = this.findByEmail(email);

		if (currentUser == null) {
			throw new IdInvalidException("User not found!");
		}

		return currentUser;
	}

	public boolean isEmailExist(String email) {
		return userRepository.existsByEmail(email);
	}

	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}

	public User handleCreateUser(User user) {
		if (user.getRole() != null) {
			Role foundRole = roleService.fetchRoleById(user.getRole().getId());
			user.setRole(foundRole);
		}
		return userRepository.save(user);
	}

	public ResCreateUserDTO convertToResCreateUserDTO(User user) {
		ResCreateUserDTO res = new ResCreateUserDTO();
		ResCreateUserDTO.RoleUser roleUser = new ResCreateUserDTO.RoleUser();
		res.setId(user.getUserId());
		res.setFullName(user.getFullName());
		res.setEmail(user.getEmail());
		res.setPhone(user.getPhone());

		if (user.getRole() != null) {
			roleUser.setRoleId(user.getRole().getId());
			roleUser.setRole(String.valueOf(user.getRole().getName()));
			res.setRole(roleUser);
		}

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
		res.setAvatar(user.getAvatar());
		res.setDob(user.getDob());
		res.setGender(user.getGender());
		res.setUpdatedAt(user.getUpdatedAt());

		return res;
	}
}
