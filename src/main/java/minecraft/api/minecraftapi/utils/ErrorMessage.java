package minecraft.api.minecraftapi.utils;

import org.bukkit.ChatColor;

public enum ErrorMessage {

    NO_PERMISSION(ChatColor.RED  + "No Permission!"){
        @Override
        public String getMessage() {
            return super.getMessage();
        }
    },
    OPERATOR_NEEDED(ChatColor.RED + "You need to be an operator to use this command!"){
        @Override
        public String getMessage() {
            return super.getMessage();
        }
    },
    INVALID_ARGUMENTS(ChatColor.RED + "Invalid Arguments!"){
        @Override
        public String getMessage() {
            return super.getMessage();
        }
    },
    RUNTIME_EXCEPTION(ChatColor.RED + "There was a problem performing this command!"){
        public String getMessage() {
            return super.getMessage();
        }
    },
    COMMAND_UNDERDEVELOPMENT(ChatColor.RED + "This command is currently under development!"){
        @Override
        public String getMessage() {
            return super.getMessage();
        }

    };


    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

