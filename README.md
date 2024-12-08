# Punishment Manager
###### A commission developed by KingsDev

![Screenshot of ban logs](https://github.com/user-attachments/assets/a8af6510-9808-4ad6-90cf-7a19afba8487)
###### To see more of my work, including more screenshots, go to https://kingrabbit.dev/

PunishmentManager is a Minecraft plugin designed to manage all your punishment needs.  The plugin allows server stuff to kick, mute, ban, and IP ban users either temporarily or permanently.  All punishment actions are logged and can be viewed with `/history`.  All chat messages are configurable via the `config.yml` found in the `plugins` folder.  Each individual command has its own permission, allowing for full customisation of what each staff rank can do.  Tab completion only works for users with the correct permissions.  When typing in arguments, the user's currently-typed command is colour coded to help them understand the command they are going to run.  All commands except for `/kick` work with offline players.  Users may only have one active ban / mute at a time.  To improve performance, active punishments are cached so database calls are not needed for every chat message / user login.  IP bans work by blocking access to the server from all IPs that IP banned players have connected from.

**Video demonstration:** [https://youtu.be/KiS5SUbkiZw](https://youtu.be/Z5NjlcuM8zA)

## Commands
`<>` required parameter  
`[]` optional parameter

### `/history menu <player>` - `punishmentManager.history`
(Player only) View a menu showcasing a player's punishment history and active punishments.

### `/history chat <player>` - `punishmentManager.history`
View a list of a player's punishment history and active punishment

### `/kick <player> [reason]` - `punishmentManager.kick`
Kick a player from the server, optionally specifying a reason.

### `/tempmute <player> <duration> [reason]` - `punishmentManager.tempmute`
Temporarily mute a player, optionally specifying a reason.

### `/mute <player> [reason]` - `punishmentManager.mute`
Permanently mute a player, optionally specifying a reason.

### `/unmute <player>` - `punishmentManager.unmute`
Unmute a player.

### `/tempban <player> <duration> [reason]` - `punishmentManager.tempban`
Temporarily ban a player, optionally specifying a reason.

### `/ban <player> [reason]` - `punishmentManager.ban`
Permanently ban a player, optionally specifying a reason.

### `/ipban <player> [reason]` - `punishmentManager.ipban`
Permanently IP ban a player, optionally specifying a reason.

### `/unban <player>` - `punishmentManager.unban`
Unban a player.

## Database settings
The plugin requires a MongoDB database to store punishment data.  This is configured by placing a `.env` file in the server's root directory containing the following environment variables:
- `MONGO_URI` - The MongoDB URI the bot should connect to.  This database will be used to store the punishments.
- `MONGO_DATABASE` - The name of the database to use
- `ENVIRONMENT` - Should be `PROD` or `DEV`.  If set to `DEV`, then the name of the database used will have `-dev` appended to it.
