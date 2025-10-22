import { A } from "@solidjs/router"
import { Box, Stack } from "@suid/material"

export const Sidebar = () => {
    return (
        <Box>
            <nav>
                <Stack>
                    <A href="/">Home</A>
                    <A href="/Sprint">Sprint</A>
                </Stack>
            </nav>
        </Box>)
}