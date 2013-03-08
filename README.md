---- Buycraft Plugin ------------------------

For setup instructions please visit http://buycraft.net.

Purchase Premium and gain access to over 15 more awesome features. 
To purchase Premium, go to http://server.buycraft.net/premium.

Are you in need of help? Please contact customer support via email (support@buycraft.net) or via the helpdesk at http://support.buycraft.net.


--- INSTALLATION --- ------------------------------------------------------

Please run the command "/buycraft secret <Secret key>" in the console. To find your 
secret key, go to http://server.buycraft.net and visit the servers page under the webstore 
section. Refer to installation videos and tutorials available on http://buycraft.net for more help/info.


---- MCMYADMIN INTERGRATION ------------------------------------------------

If you are aiming to use McMyAdmin commands in packages, prefix the commands you enter with "{mcmyadmin}", for example,
to execute the McMyAdmin "stop" command, you would enter this: "{mcmyadmin}stop". After you have setup the commands, you need
to give administrator access to the user "Buycraft" (Case sensitive) in the McMyAdmin permissions page. The user "Buycraft" has
been disabled from logging in to your server to prevent administrator access in game.


---- CHANGING THE /BUY COMMAND ---------------------------------------------

If you are using a plugin which already uses the /buy command, follow the instructions below to fix this problem:
	
	1. Download a program such as WinRar which is capable of opening .jar files.
	2. Open the Buycraft.jar file with the archive program you previously downloaded.
	3. Inside Buycraft.jar, you will find a file called plugin.yml, open this file with a text editor.
	4. Scroll down to line 7 of the plugin.yml file, and change "buy" to either "donate", "store", "shop", or "purchase".
	5. Open language.conf  which is located in the Buycraft directory, and change the "mainCommand" variable to the command which you chose above.
	6. Save all modified files and restart your server - job done!


---- PERMISSION NODES -----------------------------------------------------

Listed below are the permission nodes for the plugin:

	buycraft.admin - Enables use of the "/buycraft <reload/forcecheck/secret>" commands


---- A FURTHER NOTE --------------------------------------------------------

Modifying the source code is allowed. You are not allowed to use the source code in another Bukkit plugin without prior permission.
Use of http://api.buycraft.net is only for this plugin and integrating Buycraft on to your own website. Any other use is not allowed.


---- CHANGE LOG ------------------------------------------------------------
	
	Version 5.3
	
		- Improved how commands are executed.
	
	Version 5.2
	
		- Improved the "/buycraft secret" command in which it no longer restarts the server upon execution.
		- More threading improvements (Mainly to the authentication of the secret key).
		- If the plugin fails to start an error code will now be displayed specifying the reason.
		- URL shortener has been improved with better error responses.
		
	Version 5.1
	
		- Threading improvements
	
	Version 5.0
	
		- Fix for the "Package not found" bug.