import { Drawer, Form, Input } from "antd";
import { ProjectModel } from "../../../../../types/backend-api/project";
import { useForm } from "antd/es/form/Form";

interface AddOrUpdateProjectDrawerProps {
    /**
     * Set the drawer to be shown
     */
    show: boolean,

    /**
     * If passed, this drawer will update the data of the given project
     */
    source?: ProjectModel,

    /**
     * Callback triggered when this drawer is closed by an user event
     * @returns 
     */
    onClose?: () => void,

    /**
     * Callback triggered when a project was successfully created on this component
     * @param project 
     * @returns 
     */
    onProjectCreatedOrUpdated?: (project: ProjectModel) => void,
}

interface AddOrUpdateProjectForm {

    /**
     * The name of the project
     */
    name: string,

    /**
     * The profile name of the project
     */
    profileName: string,
}

export default function AddOrUpdateProjectDrawer({ ...props }: AddOrUpdateProjectDrawerProps) {
    const [form] = useForm<AddOrUpdateProjectForm>()

    function addOrUpdateProject() {

    }

    return (
        <Drawer
            onClose={props.onClose}
            open={props.show}
            title={"Adicionar novo projeto"}
            width={620}
        >
            <Form
                form={form}
                name="add-or-update-project-form"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                onFinish={addOrUpdateProject}
                autoComplete="true"
                size="large"
            >
                <Form.Item<AddOrUpdateProjectForm>
                    label="Nome do projeto"
                    name="name"
                    rules={[{ required: true, message: "Campo obrigatório" }]}
                >

                    <Input autoFocus={true} />
                </Form.Item>
            </Form>
        </Drawer>
    )
}