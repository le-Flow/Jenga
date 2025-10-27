import { Dialog, DialogTitle, DialogContent, Stack, TextField, DialogActions, Button } from "@suid/material"
import { Setter, useContext, createSignal } from "solid-js"
import { CreateProjectDTO, ProjectDTO, ProjectResourceService } from "../api"
import { ProjectContext } from "../provider/ProjectProvider"

interface NewProjectDialogProps {
    open: boolean
    setOpen: Setter<boolean>
}

export const NewProjectDialog = (props: NewProjectDialogProps) => {

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
        <Dialog open={props.open} fullWidth>
            <DialogTitle>New Project</DialogTitle>
            <DialogContent>
                <Stack spacing={1}>
                    <TextField name="id" label="identifier" value={id()} onChange={(_, value) => { setId(value) }}></TextField>
                    <TextField name="name" label="name" value={name()} onChange={(_, value) => { setName(value) }}></TextField>
                    <TextField name="description" label="description" value={desc()} onChange={(_, value) => { setDesc(value) }} rows={5} multiline></TextField>
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