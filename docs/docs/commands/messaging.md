# Messaging Commands

CraftUtils provides private messaging functionality with reply capabilities for easy player communication.

## Private Messaging

### `/msg <player> <message>`
**Aliases:** `/message`, `/tell`, `/cu msg`, `/w`, `/whisper`  
**Permission:** `CraftUtils.message`  
**Description:** Send a private message to another player

**Usage:**
- `/msg Steve Hello there!` - Sends "Hello there!" privately to Steve
- `/tell Alex Thanks for the help` - Alternative command to message Alex
- `/w PlayerName Secret message` - Whisper privately to PlayerName

**Features:**
- Messages are only visible to sender and recipient
- Prevents messaging yourself
- Automatically tracks last conversation partner for replies
- Color-coded format for easy identification

**Message Format:**
- **For sender:** `MSG » You ➠ PlayerName: message`
- **For recipient:** `MSG » PlayerName ➠ You: message`

## Reply System

### `/r <message>`
**Aliases:** `/reply`, `/cu reply`  
**Permission:** `CraftUtils.reply`  
**Description:** Reply to the last person who messaged you

**Usage:**
- `/r Thanks!` - Replies to your most recent conversation partner
- `/reply See you later` - Alternative reply command

**Features:**
- Automatically sends message to last person you exchanged messages with
- No need to remember or type player names
- Works both ways - you can reply to someone who messaged you, or to someone you messaged
- Handles player disconnections gracefully

## Message Management

**Conversation Tracking:**
- System remembers your last messaging partner
- Reply target is updated each time you send/receive a message
- Player disconnections automatically clear conversation history
- Each player can have different reply targets

**Safety Features:**
- Validates that target player is online before sending
- Prevents self-messaging
- Clears conversation data when players disconnect
- Handles offline/invalid players gracefully

## Configuration

Enable/disable these commands in your `config.yml`:

```yaml
commands:
  message: true
  reply: true
```

## Tips

1. **Quick Conversations:** Use `/r` for back-and-forth conversations instead of typing names repeatedly
2. **Multiple Conversations:** Remember that your reply target changes with each new conversation
3. **Staff Communication:** Great for private communication between staff members
4. **Player Support:** Useful for helping players privately without cluttering public chat

## Message Colors

The messaging system uses a consistent color scheme:
- **MSG** prefix: Green and bold
- **Player names:** Blue (sender) and Green (recipient)  
- **Arrows (➠):** Dark gray
- **Message text:** Gray

## Troubleshooting

**"You can't message yourself":**
- You tried to send a message to yourself
- Use public chat or find another player to message

**"The player you are replying to is no longer online":**
- Your last conversation partner has disconnected
- Start a new conversation with `/msg <player> <message>`

**"You don't have anyone to reply to":**
- You haven't sent or received any messages this session
- Use `/msg` to start a conversation first

**Messages not sending:**
- Verify the target player is online and spelled correctly
- Check that you have the `CraftUtils.message` permission
- Ensure messaging commands are enabled in server configuration

## Examples

**Starting a conversation:**
```
/msg Steve Hey, are you free to help with building?
```

**Quick reply:**
```
/r Sure! I'll be right over
```

**Another reply:**
```
/r Meet me at spawn
```

**New conversation:**
```
/tell Alex Thanks for the diamonds!
```

**Reply to Alex:**
```
/r No problem, happy to help!
```