package net.blaklizt.streets.core.authentication;

import net.blaklizt.streets.persistence.User;
import net.blaklizt.streets.persistence.UserGroupRole;
import net.blaklizt.streets.persistence.dao.UserDao;
import net.blaklizt.streets.persistence.dao.UserGroupRoleDao;
import net.blaklizt.symbiosis.sym_common.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
* Created with IntelliJ IDEA.
* SymbiosisUser: tkaviya
* Date: 8/6/13
* Time: 7:06 PM
*/
@Service
@Transactional(readOnly=true)
public class StreetAuthenticator {
	protected HashMap<String, List<SimpleGrantedAuthority>> grantedAuthoritiesCache = new HashMap<>();

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserGroupRoleDao userGroupRoleDao;

	private static final Logger logger = Configuration.getNewLogger(StreetAuthenticator.class.getSimpleName());

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		logger.info("Logging in user: " + username);
		User dbUser = userDao.findByUsername(username);

		if (dbUser == null) throw new UsernameNotFoundException("Could not find username " + username);

		boolean active;

		if (dbUser.getStatus() == User.UserStatus.ACTIVE.getValue()) active = true;
		else
		{
			active = false;
			logger.warning("Cannot login " + dbUser.getUsername() + " : Account is not active.");
		}

		return new org.springframework.security.core.userdetails.User(username, dbUser.getPassword(),
				active, active, active, active, getAuthorities(dbUser.getUserGroupID()));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(String userGroup)
	{
		List<SimpleGrantedAuthority> authList = new ArrayList<>();

		if (!grantedAuthoritiesCache.containsKey(userGroup))
		{
			logger.fine("Getting authorities for access group " + userGroup);

			List<UserGroupRole> userGroupRoles = userGroupRoleDao.findByUserGroup(userGroup);

			for (UserGroupRole userGroupRole : userGroupRoles)
			{
				logger.fine("Caching role " + userGroupRole.getRoleID());
				authList.add(new SimpleGrantedAuthority(userGroupRole.getRoleID()));
			}

			//cache the authorities to avoid future db hits.
			grantedAuthoritiesCache.put(userGroup, authList);
		}
		return grantedAuthoritiesCache.get(userGroup);
	}
}
