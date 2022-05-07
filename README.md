[![StandWithUkraine](https://raw.githubusercontent.com/vshymanskyy/StandWithUkraine/main/badges/StandWithUkraine.svg)](https://github.com/vshymanskyy/StandWithUkraine/blob/main/docs/README.md)
[![Stand With Ukraine](https://raw.githubusercontent.com/vshymanskyy/StandWithUkraine/main/banner2-direct.svg)](https://vshymanskyy.github.io/StandWithUkraine/)

# TFProtocol (Transfer Protocol)

The TFProtocol works by sending text commands from client to the
server in a TCP connection. Every time a command is received at the
server it responds with a command execution status. TFProtocol has
evolved from its origin to become a modular meta-protocol that allows
the addition of any type of module to gain functionalities. Those
functionalietes goes from the main core -that allows text commands
execution- to complex modules that give you the posiblity to implement
chat applications or even execute any thrid-party modules at the
server side written in any language. Example of those modules are the
support added to run the main databases services like PostgreSQL or
MySQL.

With TFProtocol you can develop Android Apps that can be integrated
through network communication with other apps or services like web
sites or IoT devices. The frontiers of what you may do with TFProtocol
and in particular with its ACE Subsystem it is only limited by your
imagination.

Services you can rent from us:

-Space in a shared VPS with TFProtocol and other services on demand,
this way you can develop applications that interact among them.
-Subdomains, this way you can use a domain name for your developments
instead of an ip address.
-Shared o dedicated VPN service on a VPS with a powerfull console
administration that allows you to create easly any number of profiles.
-Technical Support and assistance.

If you require any of our services you can contact us at: 
* lmdelbahia@gmail.com
* n4b3ts3@gmail.com
## Sandbox How-To-Use
For testing the capacities of this protocol you can try our sandbox (it is free), is located in an isolate space with low capacities giving you a lot of features explained in the documents above, you may notice that those documents explains which data are transfer with each command you execute... When you are instancing any tfprotocol class you may give the next data to connect to sandbox (For production you must read the production section).</br>
* IP/Domain Name: tfproto.expresscuba.com
* TCP Port: 10999
* Public Key: 
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlhjLEFcJQBkXlyP46utJ
JTrcE2v8KFXVwjAjJ5aiHuNmUBzo9pYLSLqr4ZDbmsmrEBlyZvL2O7wYqjqw3UnR
dQCxhxfWeNSV3uXKEDDJQdkpbUpSvmNFXL0JZHDX1HHQ/LNPzEj+bv2jFyq2H+ic
WNjv0LWeI5iIpip2H3U55POiog822CRglNjcARWfbh0mWlAs3hHIbac9Ar2DK3gn
uqRPPIYMbUYcify/gJrm50Gls1v+zaH+mcMB7VqSvDzskOm34NoL2KNtjZTZnM6k
x0j5+s8dfqPXcgmO/YyOr002ijLUPE/PPV5zsIyObze/Z/ONGtO5Bd9ZhnpZCEEO
zQIDAQAB
-----END PUBLIC KEY-----
* Hash: testhash
* Version: 0.0
* Length: 36

Extended Arbitrary Code Execution or ACE allows only to execute the next binaries inside the sandbox (in production you can execute any binary you want, so you are able to run any web backend like Django, .NET CORE, etc...) The <b >inskey</b> for access to ACE it is <b>tfproto</b>
<br>
* /bin/ls
* /bin/echo
 
Parameters for /bin/ls must be: /bin/ls /

Parameters for /bin/echo can be anyone

## Production
For use tfprotocol for a production environment you have to contact directly with us at:
* lmdelbahia@gmail.com
* n4b3ts3@gmail.com
