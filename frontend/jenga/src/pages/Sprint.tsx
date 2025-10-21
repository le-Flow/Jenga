import { Add } from "@suid/icons-material"
import { Button, Card, CardActions, CardContent, CardHeader, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, InputLabel, IconButton, List, ListItem, ListItemButton, ListItemText, MenuItem, Select, Stack, TextField } from "@suid/material"
import { createSignal, For } from "solid-js"
import { TicketDTO, TicketPriority, TicketSize, TicketStatus } from "../api"

interface BacklogProps {
    tickets: TicketDTO[]
}

const Backlog = (props: BacklogProps) => {

    const [open, setOpen] = createSignal(false)

    const [title, setTitle] = createSignal("")
    const [desc, setDesc] = createSignal("")
    const [prio, setPrio] = createSignal(TicketPriority.MEDIUM)
    const [size, setSize] = createSignal(TicketSize.MEDIUM)
    const [status, setStatus] = createSignal(TicketStatus.OPEN)
    const [assignee, setAssignee] = createSignal("")


    return (
        <>
            <Card>
                <CardHeader title="Backlog"></CardHeader>
                <CardContent>
                    <List>
                        <For each={props.tickets}>
                            {
                                (t) => {
                                    return <ListItem>
                                        <ListItemButton>
                                            <ListItemText></ListItemText>
                                        </ListItemButton>
                                    </ListItem>
                                }
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
            <Dialog open={open()} fullWidth>
                <DialogTitle title="New Ticket">New Ticket</DialogTitle>
                <DialogContent>
                    <Stack spacing={1}>
                        <TextField name="title" label="title" value={title()} onChange={(event) => { setTitle(event.target.value) }}></TextField>
                        <TextField name="description" label="description" value={desc()} onChange={(event) => { setDesc(event.target.value) }} multiline></TextField>
                        <TextField name="assignee" label="assignee" value={assignee()} onChange={(event) => { setAssignee(event.target.value) }}></TextField>
                        <FormControl fullWidth>
                            <InputLabel id="prio-label">Priority</InputLabel>
                            <Select
                                labelId="prio-label"
                                value={prio()}
                                label="Priority"
                                onChange={(e) => setPrio(e.target.value as TicketPriority)}
                            >
                                <For each={Object.values(TicketPriority)}>
                                    {(p) => <MenuItem value={p}>{p}</MenuItem>}
                                </For>
                            </Select>
                        </FormControl>

                        <FormControl fullWidth>
                            <InputLabel id="size-label">Size</InputLabel>
                            <Select
                                labelId="size-label"
                                value={size()}
                                label="Size"
                                onChange={(e) => setSize(e.target.value as TicketSize)}
                            >
                                <For each={Object.values(TicketSize)}>
                                    {(p) => <MenuItem value={p}>{p}</MenuItem>}
                                </For>
                            </Select>
                        </FormControl>

                        <FormControl fullWidth>
                            <InputLabel id="status-label">Status</InputLabel>
                            <Select
                                labelId="status-label"
                                value={status()}
                                label="Status"
                                onChange={(e) => setStatus(e.target.value as TicketStatus)}
                            >
                                <For each={Object.values(TicketStatus)}>
                                    {(p) => <MenuItem value={p}>{p}</MenuItem>}
                                </For>
                            </Select>
                        </FormControl>
                    </Stack>

                </DialogContent>
                <DialogActions>
                    <Button onClick={() => { setOpen(false) }}>cancel</Button>
                    <Button onClick={() => { setOpen(false) }}>create</Button>
                </DialogActions>
            </Dialog>
        </>
    )
}

const Kanban = () => {
    return (
        <Card>
            <CardHeader title="Kanban"></CardHeader>
            <CardContent></CardContent>
        </Card>
    )
}

export const Sprint = () => {
    const tickets: TicketDTO[] = []

    return (
        <Card sx={{}}>
            <CardHeader title="Sprint"></CardHeader>
            <CardContent>
                <Stack spacing={2}>
                    <Kanban></Kanban>
                    <Backlog tickets={tickets}></Backlog>
                </Stack>

            </CardContent>
        </Card>
    )
}