import { Add } from "@suid/icons-material"
import { Button, Card, CardActions, CardContent, CardHeader, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, InputLabel, IconButton, List, ListItem, ListItemButton, ListItemText, MenuItem, Select, Stack, TextField, Table, TableContainer, TableBody, TableHead, TableCell, TableRow, Paper } from "@suid/material"
import { createMemo, createSignal, For, useContext } from "solid-js"
import { CreateTicketDTO, TicketDTO, TicketPriority, TicketResourceService, TicketSize, TicketStatus, User } from "../api"
import { ProjectContext } from "../provider/ProjectProvider"

interface BacklogProps {
    tickets: TicketDTO[]
}

const Backlog = (props: BacklogProps) => {

    const pCtx = useContext(ProjectContext)

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
                        <For each={pCtx?.tickets() ?? []}>
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
                    <Button onClick={() => {
                        const createTicketDTO: CreateTicketDTO = {
                            projectName: pCtx?.selectedProject().name,
                            title: title(),
                            description: desc(),
                            priority: prio(),
                            size: size(),
                            status: status(),
                            assigneeName: assignee(),
                        }
                        const ticket: TicketDTO = {
                            ...createTicketDTO
                        }
                        TicketResourceService.postApiProjectsTickets(pCtx?.selectedProject().identifier ?? "", createTicketDTO)
                        pCtx?.setTickets([...pCtx?.tickets() ?? [], ticket])
                        setOpen(false)

                    }}>create</Button>
                </DialogActions>
            </Dialog>
        </>
    )
}

interface RowProps {
    dev: string
    tickets?: TicketDTO[]
}

const Row = (props: RowProps) => {
    return (
        <TableRow>
            <TableCell>{props.dev}</TableCell>
            <TableCell></TableCell>
            <TableCell></TableCell>
            <TableCell></TableCell>
            <TableCell></TableCell>
        </TableRow>
    )
}


const Kanban = () => {

    const pCtx = useContext(ProjectContext)

    const tickets = createMemo(() =>
        Map.groupBy(pCtx?.tickets() ?? [], t => t.assigneeName ?? "")
    )

    return (
        <Card>
            <CardHeader title="Kanban"></CardHeader>
            <CardContent>
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Dev</TableCell>
                                <TableCell>Todo</TableCell>
                                <TableCell>In Progress</TableCell>
                                <TableCell>In Review</TableCell>
                                <TableCell>Done</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            <For each={[...tickets().entries()]}>
                                {
                                    t => <Row dev={t[0]} tickets={t[1]}></Row>
                                }
                            </For>
                        </TableBody>
                    </Table>
                </TableContainer>
            </CardContent>
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