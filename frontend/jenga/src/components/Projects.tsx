import { Button, Card, CardActions, CardContent, CardHeader, Dialog, DialogActions, DialogContent, DialogTitle, List, ListItem, ListItemButton, ListItemText, Stack, TextField } from "@suid/material"
import { ProjectContext } from "../provider/ProjectProvider"
import { createSignal, For, useContext } from "solid-js"


const NewProject = () => {
    return (
        <></>
    )
}

export const Projects = () => {

    const [open, setOpen] = createSignal(false)

    const [name, setName] = createSignal("")
    const [desc, setDesc] = createSignal("")

    const projectCtx = useContext(ProjectContext)

    const onCreate = () => {

    }

    return (
        <>
            <Card>
                <CardHeader title="Projects" />
                <CardContent>
                    <List>
                        <For each={projectCtx?.projects() ?? []}>
                            {
                                (p) => {
                                    return (
                                        <ListItem>
                                            <ListItemButton>
                                                <ListItemText
                                                    primary={p.name}
                                                    secondary={((p.createDate ?? "") + " | " + (p.modifyDate ?? ""))}
                                                />
                                            </ListItemButton>
                                        </ListItem>
                                    )
                                }
                            }
                        </For>
                    </List>
                </CardContent>
                <CardActions>
                    <Button onClick={() => { setOpen(true) }}>
                        NEW
                    </Button>
                </CardActions>
            </Card>
            <Dialog open={open()}>
                <DialogTitle>New Project</DialogTitle>
                <DialogContent>
                    <Stack spacing={1}>
                        <TextField name="name" label="name" value={name()} onChange={(_, value) => { setName(value) }}></TextField>
                        <TextField name="description" label="description" value={desc()} onChange={(_, value) => { setDesc(value) }} multiline></TextField>
                    </Stack>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => { setOpen(false)}}>
                        cancel
                    </Button>
                    <Button onClick={() => { 

                        setOpen(false)
                    }}>
                        create
                    </Button>
                </DialogActions>

            </Dialog>
        </>
    )
}