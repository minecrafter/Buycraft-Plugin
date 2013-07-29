---- Buycraft Plugin ------------------------

For setup instructions please visit http://buycraft.net.

Purchase Premium and gain access to over 15 more awesome features. 
To purchase Premium, go to http://server.buycraft.net/premium.

Are you in need of help? Please contact customer support via email (support@buycraft.net) or via the helpdesk at http://support.buycraft.net.


--- INSTALLATION -----------------------------------------------------------

Please run the command "/buycraft secret <Secret key>" in the console. To find your 
secret key, go to http://server.buycraft.net and visit the servers page under the webstore 
section. Refer to installation videos and tutorials available on http://buycraft.net for more help/info.

---- RECENT PAYMENT SIGNS --------------------------------------------------

If you would like to display recent payments in game, this feature allows you to
setup a row of signs with head blocks placed above/below/next to them. Every 30 minutes
the signs will update and show the most recent users who have purchased a package.
To enable this feature you need to set "headsEnabled" to "true" in the settings.conf file.

Place a row of signs in any order you wish, with one head per sign at least one block away. 
After you have placed the signs and heads, type "buysign begin" in game. Then click on the signs
in the order you wish the payments to be displayed. Once you have finished, type "buysign end" - 
the signs will update accordingly.

You can also filter the signs to only include payments that include a certain package. 
Use "buysign filter <Package ID>" after typing "buysign begin" to do this. 

---- MCMYADMIN INTERGRATION ------------------------------------------------

If you are aiming to use McMyAdmin commands in packages, prefix the commands you enter with "{mcmyadmin}", for example,
to execute the McMyAdmin "stop" command, you would enter this: "{mcmyadmin}stop". After you have setup the commands, you need
to give administrator access to the user "Buycraft" (Case sensitive) in the McMyAdmin permissions page. The user "Buycraft" has
been disabled from logging in to your server to prevent administrator access in game.


---- CHANGING THE /BUY COMMAND ---------------------------------------------

To change the /buy command please edit the setting in the configuration file.

---- PERMISSION NODES -----------------------------------------------------

Listed below are the permission nodes for the plugin:

	buycraft.admin - Enables use of the "/buycraft <reload/forcecheck/secret>" commands
	buycraft.signs - Enables the player to setup Buycraft recent payment signs


---- A FURTHER NOTE --------------------------------------------------------

Modifying the source code is allowed. You are not allowed to use the source code in another Bukkit plugin without prior permission.
Use of http://api.buycraft.net is only for this plugin and integrating Buycraft on to your own website. Any other use is not allowed.


---- CHANGE LOG ------------------------------------------------------------
	
	Version 5.8

		- Fixed a bug with case sensitive usernames.

	Version 5.7

		- Major performance improvements over the entire plugin.
		- New /buycraft report command, this will assist our customer support in helping you resolve any future issues.

	Version 5.6

		- Implemented a thread pool to improve performance on larger servers
		- You can now display recent payments in game! Read above to find out how to do this.
		- New "/buycraft payments <ign>"" command, will list the payments over all users or a specific user.

	Version 5.5

		- Fixes an exception with the /buycraft command.

	Version 5.4
	
		- Changing the /buy command is now a simple option in the settings file.
		- Improved how commands are executed (Thread-safe, should stop some bugs with certain plugins).
	
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