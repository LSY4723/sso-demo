package com.guoanjia.guoanjiasso.dao.jpa;

import com.guoanjia.guoanjiasso.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * UserReporstiy
 *
 * @author syliu
 * @create 2018/1/30 0030
 */
@Repository
public interface UserRepository extends JpaRepository<User,String> {
	User findByOpenid(String param);

	User findByEmail(String param);

	User findByPhone(String param);

	User findByUsername(String param);
}
