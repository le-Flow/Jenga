import { Dialog, DialogTitle, DialogContent, DialogActions, Button } from "@suid/material"
import { Setter, useContext, createSignal, createEffect } from "solid-js"
import { CreateProjectDTO, ProjectDTO, ProjectResourceService } from "../api"
import { ProjectContext } from "../provider/ProjectProvider"
import { ProjectInfo } from "./ProjectInfo"

interface NewProjectDialogProps {
    open: boolean
    setOpen: Setter<boolean>
}

export const NewProjectDialog = (props: NewProjectDialogProps) => {

    const pCtx = useContext(ProjectContext)

    const EMPTY_PROJECT: ProjectDTO = { identifier: "", name: "", description: "" }
    const formId = "new-project-form"
    const [project, setProject] = createSignal<ProjectDTO>({ ...EMPTY_PROJECT })

    createEffect(() => {
        if (props.open) setProject(() => ({ ...EMPTY_PROJECT }))
    })

    const onCreate = (draft?: ProjectDTO) => {
        const source = draft ?? project()
        const request: CreateProjectDTO = {
            identifier: source.identifier ?? "",
            name: source.name ?? "",
            description: source.description ?? ""
        }
        ProjectResourceService.postApiProjects(request)
        pCtx?.setProjects(prev => [...(prev ?? []), { ...request }])
        props.setOpen(false)
    }

    return (
        <Dialog open={props.open} fullWidth>
            <DialogTitle>New Project</DialogTitle>
            <DialogContent>
                <ProjectInfo
                    formId={formId}
                    project={project()}
                    onProjectChange={setProject}
                    onSubmit={onCreate}
                />
            </DialogContent>
            <DialogActions>
                <Button type="button" onClick={() => { props.setOpen(false) }}>
                    cancel
                </Button>
                <Button type="submit" form={formId}>
                    create
                </Button>
            </DialogActions>
        </Dialog>
    )
}
