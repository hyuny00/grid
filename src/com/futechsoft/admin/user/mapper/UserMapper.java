package com.futechsoft.admin.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.futechsoft.admin.user.vo.UserRole;
import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

@Mapper("user.mapper.UserMapper")
public interface UserMapper  {



	/**
	 * 유저 목록 리스트
	 * @param params
	 * @throws Exception
	 */
	List<FtMap> selectUserList(@Param("pageable") Pageable pageable, @Param("params") FtMap params) throws Exception;
	/**
	 * 유저 목록 리스트 카운트
	 * @param params
	 * @throws Exception
	 */
	long countUserList(@Param("params") FtMap params) throws Exception;

	/**
	 * 유저의 권한 리스트
	 * @param params
	 * @throws Exception
	 */
	List<UserRole> getUserRoleList(FtMap params) throws Exception;

	/**
	 * 유저 상세 정보
	 * @param params
	 * @throws Exception
	 */
	FtMap detailUser(FtMap params) throws Exception;




	/**
	 * 유저 권한 등록
	 * @param params
	 * @throws Exception
	 */
	void insertUserRole(FtMap params) throws Exception;
	
	/**
	 * 유저  삭제
	 * @param params
	 * @throws Exception
	 */
	void deleteUser(FtMap params) throws Exception;

	/**
	 * 유저 권한 삭제
	 * @param params
	 * @throws Exception
	 */
	void deleteUserRole(FtMap params) throws Exception;



	/**
	 * 업무 권한 리스트
	 * @param params
	 * @throws Exception
	 */
	List<FtMap> selectRoleList(FtMap params) throws Exception;


	void insertUser(FtMap params) throws Exception;
	
	
	void updateUser(FtMap params) throws Exception;
}
