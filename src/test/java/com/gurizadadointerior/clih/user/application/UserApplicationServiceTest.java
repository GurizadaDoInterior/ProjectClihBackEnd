package com.gurizadadointerior.clih.user.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.gurizadadointerior.clih.shared.error.ApiException;
import com.gurizadadointerior.clih.shared.error.ErrorCode;
import com.gurizadadointerior.clih.user.domain.AppUser;
import com.gurizadadointerior.clih.user.domain.AppUserRepository;
import org.junit.jupiter.api.Test;

class UserApplicationServiceTest {

	private final UserApplicationService service = new UserApplicationService(mock(AppUserRepository.class));

	@Test
	void rejectsNonHttpsAvatarUrl() {
		var command = new UpdateUserCommand(null, null, "http://example.com/avatar.png", null, null, null);

		assertThatThrownBy(() -> service.update(new AppUser("subject"), command))
			.isInstanceOf(ApiException.class)
			.extracting(exception -> ((ApiException) exception).getErrorCode())
			.isEqualTo(ErrorCode.INVALID_AVATAR_URL);
	}
}
