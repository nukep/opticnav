/* *********************************************************************
**	File:  		    findAccount.sql
**	Author:  	    Kay Bernhardt
**	Created:	    Feburary 1, 2014
**	Description:	Will search for the provided account name
**  Returns:        0 if the account name is not found
**                  The account id if the account name is found
**	Update History:
********************************************************************* */


DROP FUNCTION IF EXISTS findAccount;

DELIMITER //

CREATE FUNCTION findAccount 
(p_accountName VARCHAR(25))
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE id INT;
    SET id = 0;

    SELECT account_id INTO id
    FROM ACCOUNT
    WHERE user = p_accountName;   

    RETURN id;
END//

DELIMITER ;