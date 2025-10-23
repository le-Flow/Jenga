import { Button, Card, CardActions, CardContent, CardHeader, Dialog, DialogActions, DialogContent, DialogTitle, List, ListItem, ListItemButton, ListItemText, Stack, TextField } from "@suid/material"
import { ProjectContext } from "../provider/ProjectProvider"
import { createMemo, createSignal, For, Setter, useContext } from "solid-js"
import { ProjectResourceService, CreateProjectDTO, ProjectDTO } from "../api"


interface NewProjectDialogProps {
    open: boolean
    setOpen: Setter<boolean>
}

const NewProjectDialog = (props: NewProjectDialogProps) => {

    const pCtx = useContext(ProjectContext)

    const [id, setId] = createSignal("")
    const [name, setName] = createSignal("")
    const [desc, setDesc] = createSignal("")

    const onCreate = () => {
        const request: CreateProjectDTO = {
            identifier: id(),
            name: name(),
            description: desc()
        }
        const newProject: ProjectDTO = {
            ...request
        }
        ProjectResourceService.postApiProjects(request)
        pCtx?.setProjects(prev => [...(prev ?? []), { ...request }])
        props.setOpen(false)
    }

    return (
        <Dialog open={props.open}>
            <DialogTitle>New Project</DialogTitle>
            <DialogContent>
                <Stack spacing={1}>
                    <TextField name="id" label="identifier" value={id()} onChange={(_, value) => { setId(value) }}></TextField>
                    <TextField name="name" label="name" value={name()} onChange={(_, value) => { setName(value) }}></TextField>
                    <TextField name="description" label="description" value={desc()} onChange={(_, value) => { setDesc(value) }} multiline></TextField>
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => { props.setOpen(false) }}>
                    cancel
                </Button>
                <Button onClick={() => {
                    onCreate()
                }}>
                    create
                </Button>
            </DialogActions>
        </Dialog>
    )
}

export const Projects = () => {

    const pCtx = useContext(ProjectContext)

    const [open, setOpen] = createSignal(false)

    const projectCtx = useContext(ProjectContext)

    const projects = createMemo(() => {
        if (projectCtx?.projects.error) return

        return (projectCtx?.projects())
    })

    return (
        <>
            <Card sx={{ "height": "100%" }}>
                <CardHeader title="Projects" />
                <CardContent sx={{ "height": "80%" }}>
                    <List sx={{ "flex": "1", "height": "100", "maxHeight": "100%", "overflow": "auto" }}>
                        <For
                            each={projects()}
                            fallback={<div>No projects found</div>
                            }
                        >
                            {
                                (p) => {
                                    return (
                                        <ListItem>
                                            <ListItemButton onClick={() => { pCtx?.setSelectedProject(p) }}>
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
            </Card >
            <NewProjectDialog open={open()} setOpen={setOpen}></NewProjectDialog>
        </>
    )
}