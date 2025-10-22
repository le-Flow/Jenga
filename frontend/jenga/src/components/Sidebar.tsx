import { A } from "@solidjs/router"
import { Box, List, ListItem, ListItemButton, Stack } from "@suid/material"

export const Sidebar = () => {
    return (
        <Box>
            <nav>
                <List>
                    <ListItem>
                        <ListItemButton component={A} href="/">Home</ListItemButton>
                    </ListItem>
                    <ListItem>
                        <ListItemButton component={A} href="/Sprint">Sprint</ListItemButton>
                    </ListItem>
                </List>
            </nav>
        </Box>)
}