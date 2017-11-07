package hu.kuncystem.patient.pojo.user;

/**
 * This interface defines the standard operations to be performed on a Doctor or a Patient model object(s).
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. nov. 7.
 *  
 * @version 1.0
 */
public interface User {
	/**
	 * Get identification of one user.
	 */
	public long getId();

	/**
	 * This is unique row id of user in database. If this id is -1 then this object doesn�t contain data of new user. 
	 * If the id is greater than -1 then this object contains data of an existing user.
	 *
	 * @param id Unique row id of user in database.
	 */
	public void setId(long id);

	/**
	 * Get user's name.
	 * */
	public String getUserName();

	/**
	 * Set user's name. This name is alias name of user. e.g.: john1978
	 *
	 * @param name This is the user�s name.
	 */
	public void setUserName(String name);

	/**
	 * Get user's password.
	 * */
	public String getPassword();

	/**
	 * Set user's password.
	 *
	 * @param psw The user's password which have to be encoded format.
	 */
	public void setPassword(String psw);

	/**
	 * Get user's full name(first name, surname)
	 * */
	public String getFullname();

	/**
	 * Set user's full name. This string contains the first name and the surname, too. 
	 *
	 * @param name The full name, e.g.: John Bush
	 *
	 * @return void
	 */
	public void setFullname(String name);

	/**
	 * Get user's email.
	 * */
	public String getEmail();

	/**
	 * Set user's email.
	 *
	 * @param email Valid email address, e.g.: john@example.com
	 */
	public void setEmail(String email);

	/**
	 * Set user's state.
	 * 
	 * @param active true: user is active, false: user is not active.
	 * */
	public void setActive(boolean active);

	/**
	 * Check the user's state.
	 * */
	boolean isActive();

	/**
	 * Get current object type. So we can define that this user is a doctor or a patient.
	 * */
	@SuppressWarnings("rawtypes")
	public Class getType();

}
