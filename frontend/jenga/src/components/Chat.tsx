import { Alert, Button, Card, CardActions, CardContent, CardHeader, Dialog, DialogActions, DialogContent, Input, List, ListItem, ListItemText, Stack, TextField } from "@suid/material"
import { createEffect, createResource, createSignal, For, Show, useContext } from "solid-js"
import { ProjectContext } from "../provider/ProjectProvider"
import { AiResourceService } from "../api"
import { LayoutContext } from "../provider/LayoutProvider"
import { UserContext } from "../provider/UserProvider"
import { AuthContext } from "../provider/AuthProvider"

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

export const Chat = () => {
    const uCtx = useContext(UserContext)
    const aCtx = useContext(AuthContext)
    const pCtx = useContext(ProjectContext)

    const [sessionId, setSessionId] = createSignal<string>()

    const [message, setMessage] = createSignal("")
    const [n, setN] = createSignal(0)

    const [response] = createResource(n, async q => await AiResourceService.postApiAiChat({
        //sessionId: sessionId() ?? undefined,
        currentUser: uCtx?.user()?.username ?? "unknown",
        currentProjectID: pCtx?.selectedProject()?.identifier ?? "",
        currentTicketID: pCtx?.selectedTicket()?.id ?? 0,
        message: message()
    }
    ))

    createEffect(() => {
        if (response()) {
            setSessionId(response()?.conversationId)
            setMessage("")
        }
    })

    const [messages] = createResource(n, async (q) => await AiResourceService.getApiAiSessionsMessages(sessionId() ?? ""));

    const [showError, setShowError] = createSignal(false);

    const onClick = () => {
        if (aCtx?.isLoggedIn?.()) {
            setShowError(false);
            setN(prev => prev + 1);
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
                        messages()
                    }>
                        {(msg) => (
                            <ListItem>
                                <ListItemText primary={`${msg.content}`} />
                            </ListItem>
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