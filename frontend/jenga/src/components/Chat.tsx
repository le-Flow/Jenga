import { Alert, Button, Card, CardActions, CardContent, CardHeader, Dialog, DialogActions, DialogContent, Divider, Input, List, ListItem, ListItemText, Stack, TextField } from "@suid/material"
import { createEffect, createResource, createSignal, For, Show, useContext } from "solid-js"
import { ProjectContext } from "../provider/ProjectProvider"
import { AiResourceService } from "../api"
import { LayoutContext } from "../provider/LayoutProvider"
import { UserContext } from "../provider/UserProvider"
import { AuthContext } from "../provider/AuthProvider"
import { AiContext } from "../provider/AiProvider"

export const ChatButton = () => {

    const layoutCtx = useContext(LayoutContext)

    const onClick = () => {
        layoutCtx?.setOpenChat(true);
    }

    return (
        <Button variant="contained" onClick={onClick}>
            Ai
        </Button>
    )
}

const dummyMessages = [
    { content: "Hello, how can I help you?" },
    { content: "I have a problem with my project." },
    { content: "Sure, what seems to be the issue?" },
]

export const Chat = () => {
    const aCtx = useContext(AuthContext)
    const aiContext = useContext(AiContext)

    const [message, setMessage] = createSignal("")

    const [showError, setShowError] = createSignal(false);

    const onClick = () => {
        if (aCtx?.isLoggedIn?.()) {
            setShowError(false);
            aiContext?.sendMessage(message());

        } else {
            setShowError(true);
        }
    }

    return (
        <Card>
            <CardHeader title="AI Chat"></CardHeader>
            <CardContent>
                <List>

                    <For each={
                        aiContext?.messages() ?? []
                    }>
                        {(msg, i) => (
                            <>
                                <ListItem >
                                    <ListItemText primary={i() % 2 === 0 ? `User: ${msg}` : `AI: ${msg}`} sx={{ "backgroundColor": i() % 2 === 0 ? "gray" : "white" }} />
                                </ListItem>
                                <Divider />
                            </>
                        )}
                    </For>
                </List>
                <TextField fullWidth value={message()} onChange={(_, value) => setMessage(value)} placeholder="Ask the AI for help..."></TextField>
            </CardContent>
            <CardActions>
                <Button onClick={onClick}>Send</Button>
                <Show when={showError()}>
                    <Alert severity="error">You must be logged in to use the AI chat.</Alert>
                </Show>
            </CardActions>

        </Card>
    )
}

export const ChatDialog = () => {

    const layoutCtx = useContext(LayoutContext);

    return (
        <Dialog open={layoutCtx?.openChat() ?? false} onClose={() => layoutCtx?.setOpenChat(false)}>
            <DialogContent>
                <Chat></Chat>
            </DialogContent>
        </Dialog>
    )
}