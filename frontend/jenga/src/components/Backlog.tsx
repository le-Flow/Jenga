import { Add } from "@suid/icons-material"
import { Card, CardHeader, CardContent, List, ListItem, ListItemButton, ListItemText, CardActions, IconButton, ListItemAvatar, Avatar } from "@suid/material"
import { useContext, createSignal, For } from "solid-js"
import { TicketDTO } from "../api"
import { ProjectContext } from "../provider/ProjectProvider"
import { NewTicketDialog } from "./NewTicketDialog"

interface BacklogItemProps {
    ticket: TicketDTO
}

const BacklogItem = (props: BacklogItemProps) => {
    const pCtx = useContext(ProjectContext)

    return (
        <ListItem draggable>
            <ListItemButton onClick={() => pCtx?.setSelectedTicket(props.ticket)}>
                <ListItemAvatar>
                    <Avatar></Avatar>
                </ListItemAvatar>
                <ListItemText
                    primary={props.ticket.title}
                    secondary={`reporter: ${props.ticket.reporterName} assignee: ${props.ticket.assigneeName}`} />
            </ListItemButton>
        </ListItem>
    )
}

interface BacklogProps {
    tickets?: TicketDTO[]
}

export const Backlog = (props: BacklogProps) => {

    const pCtx = useContext(ProjectContext)

    const [open, setOpen] = createSignal(false)

    return (
        <>
            <Card>
                <CardHeader title="Backlog"></CardHeader>
                <CardContent>
                    <List>
                        <For each={pCtx?.tickets() ?? []}>
                            {
                                (t) => <BacklogItem ticket={t}></BacklogItem>
                            }
                        </For>
                    </List>
                </CardContent>
                <CardActions>
                    <IconButton onClick={() => setOpen(true)}>
                        <Add></Add>
                    </IconButton>
                </CardActions>
            </Card>
            <NewTicketDialog open={open()} setOpen={setOpen}></NewTicketDialog>
        </>
    )
}